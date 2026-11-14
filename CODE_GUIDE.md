# Vestio Backend - Code Guide

This document provides an overview of the core architecture and coding patterns used in the Vestio Backend project.

## Architecture Overview

The project follows a standard Spring Boot layered architecture:
- **Controller Layer**: REST endpoints in `org.app.vestiobackend.controller`.
- **Service Layer**: Business logic in `org.app.vestiobackend.service`.
- **Repository Layer**: Data access using Spring Data JPA in `org.app.vestiobackend.repository`.
- **DTOs**: Data Transfer Objects in `org.app.vestiobackend.dto`.
- **Entities**: JPA Entities in `org.app.vestiobackend.entity`.
- **Mappers**: MapStruct mappers for DTO-Entity conversion in `org.app.vestiobackend.mapper`.

## Key Entities

### User
Represents a registered user.
- Fields: `id` (UUID), `firstName`, `lastName`, `email` (unique), `age`, `gender`, `city`.
- Managed via `UserRepository`.

### Listing
Represents an item listed for sale/trade.
- Fields: `title`, `description`, `price`, `quantity`, `condition`, `contactType`, `status`, `colors`.
- Relationships: `user` (Owner), `city`, `category`.
- Managed via `ListingRepository`.

## Core Services

### AuthService
Handles user signup and login integration with AWS Cognito.
- `signup(SignupRequestDto)`: Creates user in Cognito and local DB.
- `login(LoginRequestDto)`: Authenticates with Cognito and returns a JWT.
- **Local Profile**: Supports a mock authentication for local development using `password123`.

### ListingService
Handles CRUD operations for listings.
- Enforces ownership checks for update/delete operations.
- Uses `SecurityContextHolder` to identify the current authenticated user.

## Security & Authentication

- **Provider**: AWS Cognito is used for identity management.
- **JWT**: The backend acts as an OAuth2 Resource Server, validating JWTs issued by Cognito.
- **Local Development**: The `local` profile enables `LocalMockAuthFilter`, allowing authentication via a mock token header: `Authorization: Bearer mock-local-jwt-token-for-user@example.com`.

## Error Handling

- Centralized error handling in `HttpExceptionHandler`.
- Standardized response format using `ErrorResponse`.
- Custom exceptions in `org.app.vestiobackend.http.errors.exception`.

## Database Management

- **Database**: H2 for tests and dev PostgreSQL for the local profile.
- **Migrations**: Liquibase managed via `src/main/resources/db/changelog`.
- **CRITICAL**: Never change existing IDs (UUIDs) in `src/main/resources/sql/initial_data.sql`. These IDs are used as references in production and other environments. Changing them will break referential integrity and cause startup failures.

## AI & Development Guidelines

- **SQL Data**: When adding or updating static data in `initial_data.sql`, always use new, unique UUIDs for new entries and NEVER modify existing ones.
