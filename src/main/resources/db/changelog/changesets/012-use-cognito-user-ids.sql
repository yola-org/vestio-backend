-- Upgrade existing databases; fresh schemas already use the Cognito subject as users.id.
-- Keep the remapping and removal atomic, including every referencing foreign key.
DO $migration$
DECLARE
    reference record;
    user_id_column smallint;
BEGIN
    LOCK TABLE users IN ACCESS EXCLUSIVE MODE;

    IF EXISTS (
        SELECT 1 FROM users
        WHERE cognito_sub IS NULL
           OR cognito_sub !~* '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
    ) THEN
        RAISE EXCEPTION 'Cannot migrate user IDs: every existing user must have a valid Cognito UUID subject';
    END IF;
    IF (SELECT count(*) FROM users) <> (SELECT count(DISTINCT cognito_sub::uuid) FROM users) THEN
        RAISE EXCEPTION 'Cannot migrate user IDs: Cognito subjects must be unique UUIDs';
    END IF;

    SELECT attnum INTO user_id_column FROM pg_attribute
    WHERE attrelid = 'users'::regclass AND attname = 'id';
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE contype = 'f' AND confrelid = 'users'::regclass
          AND (cardinality(conkey) <> 1 OR confkey <> ARRAY[user_id_column])
    ) THEN
        RAISE EXCEPTION 'Cannot migrate user IDs: unsupported foreign key to users';
    END IF;

    CREATE TEMP TABLE vestio_user_id_map (
        old_id uuid PRIMARY KEY,
        new_id uuid NOT NULL UNIQUE,
        temporary_id uuid NOT NULL UNIQUE
    ) ON COMMIT DROP;
    INSERT INTO vestio_user_id_map SELECT id, cognito_sub::uuid, gen_random_uuid() FROM users;

    CREATE TEMP TABLE vestio_user_references ON COMMIT DROP AS
    SELECT c.conrelid::regclass AS table_name, c.conname AS constraint_name,
           a.attname AS column_name, pg_get_constraintdef(c.oid) AS definition
    FROM pg_constraint c
    JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = c.conkey[1]
    WHERE c.contype = 'f' AND c.confrelid = 'users'::regclass;

    FOR reference IN SELECT * FROM vestio_user_references LOOP
        EXECUTE format('LOCK TABLE %s IN ACCESS EXCLUSIVE MODE', reference.table_name);
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', reference.table_name, reference.constraint_name);
    END LOOP;

    -- Use temporary UUIDs first, so even swapped old/new IDs preserve ownership and uniqueness.
    FOR reference IN SELECT * FROM vestio_user_references LOOP
        EXECUTE format('UPDATE %s child SET %I = mapping.temporary_id FROM vestio_user_id_map mapping WHERE child.%I = mapping.old_id',
            reference.table_name, reference.column_name, reference.column_name);
    END LOOP;
    UPDATE users u SET id = mapping.temporary_id FROM vestio_user_id_map mapping WHERE u.id = mapping.old_id;
    UPDATE users u SET id = mapping.new_id FROM vestio_user_id_map mapping WHERE u.id = mapping.temporary_id;
    FOR reference IN SELECT * FROM vestio_user_references LOOP
        EXECUTE format('UPDATE %s child SET %I = mapping.new_id FROM vestio_user_id_map mapping WHERE child.%I = mapping.temporary_id',
            reference.table_name, reference.column_name, reference.column_name);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT %I %s',
            reference.table_name, reference.constraint_name, reference.definition);
    END LOOP;

    ALTER TABLE users DROP COLUMN cognito_sub;
END
$migration$;
