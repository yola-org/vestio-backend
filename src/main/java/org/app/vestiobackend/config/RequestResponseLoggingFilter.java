
package org.app.vestiobackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final int CONTENT_CACHE_LIMIT = 1024 * 1024; // 1 MB

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logRequest(requestWrapper);
            logResponse(requestWrapper, responseWrapper);

            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        int payloadLength = request.getContentAsByteArray().length;

        log.info(
                "[API Request] {} {} | Payload Length: {} bytes",
                method,
                uri,
                payloadLength
        );
    }

    private void logResponse(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response
    ) {
        int status = response.getStatus();
        int payloadLength = response.getContentAsByteArray().length;

        log.info(
                "[API Response] {} {} | Status: {} | Payload Length: {} bytes",
                request.getMethod(),
                request.getRequestURI(),
                status,
                payloadLength
        );
    }
}
