package dev.sijunyang.celog.surpport.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * {@link AuthenticationEntryPoint}의 구현체로, {@link AuthenticationException}을
 * {@link HandlerExceptionResolver}에 위임하여 처리합니다. {@link HandlerExceptionResolver}는 기본적으로
 * {@link SecurityExceptionHandlerAdvice}에서 예외를 해결합니다.
 *
 * @author Sijun Yang
 * @see SecurityExceptionHandlerAdvice
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authenticationException) {
        this.resolver.resolveException(request, response, null, authenticationException);
    }

}
