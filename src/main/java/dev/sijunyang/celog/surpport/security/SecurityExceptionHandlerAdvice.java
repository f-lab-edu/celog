package dev.sijunyang.celog.surpport.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Spring Security FilterChain에서 발생하는 예외를 처리하기 위한 클래스입니다. 다른 웹 예외와 동일한 방식으로 관리하기 위해 사용합니다.
 * <br/>
 * Security FilterChain은 스프링 컨테이너 바깥에서 실행되므로 DispatcherServlet에서 예외를 직접 처리할 수 없습니다. 그러나
 * {@link CustomAccessDeniedHandler}, {@link CustomAuthenticationEntryPoint}에서 예외를 이
 * 클래스의 @ExceptionHandler가 처리하도록 위임합니다.
 *
 * @author Sijun Yang
 * @see CustomAccessDeniedHandler
 * @see CustomAuthenticationEntryPoint
 */
// 이 객체가 예외처리하는 예외들은 인증/보안과 관련된 기능들이므로 의도하고 던지는 구체적인 예외가 아닌 이상,
// 클라이언트에게 정보를 노출하지 않아야 한다.
@RestControllerAdvice
public class SecurityExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleException(RuntimeException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "알수없는 문제가 발생하였습니다.");
        response.setTitle("Authentication_Unexpected_Error");
        return response;
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ProblemDetail handleException(InternalAuthenticationServiceException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "인증 중 서버 내부적인 문제가 발생하였습니다.");
        response.setTitle("Authentication_Service_Exception");
        return response;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleException(AuthenticationException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "사용자를 식별할 수 없습니다.");
        response.setTitle("Authentication_Exception");
        return response;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleException(AccessDeniedException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "사용자의 권한이 올바르지 않습니다.");
        response.setTitle("Access_Denied_Exception");
        return response;
    }

}
