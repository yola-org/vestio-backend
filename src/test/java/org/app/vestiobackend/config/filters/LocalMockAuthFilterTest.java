package org.app.vestiobackend.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LocalMockAuthFilterTest {

    private final LocalMockAuthFilter filter = new LocalMockAuthFilter();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void forwardsOtherHeadersAndSetsTheLocalPrincipal() throws Exception {
        request.addHeader("Authorization", "Bearer mock-local-jwt-token-for-  LOCAL@EXAMPLE.COM ");
        request.addHeader("X-Test", "one");
        request.addHeader("X-Test", "two");
        MockFilterChain downstream = new MockFilterChain();

        filter.doFilter(request, response, downstream);

        HttpServletRequest wrapped = (HttpServletRequest) downstream.getRequest();
        assertThat(wrapped).isNotNull();
        assertThat(wrapped.getHeader("authorization")).isNull();
        assertThat(Collections.list(wrapped.getHeaders("authorization"))).isEmpty();
        assertThat(Collections.list(wrapped.getHeaderNames())).containsExactly("X-Test");
        assertThat(wrapped.getHeader("X-Test")).isEqualTo("one");
        assertThat(Collections.list(wrapped.getHeaders("X-Test"))).containsExactly("one", "two");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("local@example.com");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"Bearer regular-token", "Bearer mock-local-jwt-token-for- "})
    void leavesOtherRequestsUntouched(String authorization) throws Exception {
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        FilterChain downstream = mock(FilterChain.class);

        filter.doFilter(request, response, downstream);

        verify(downstream).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
