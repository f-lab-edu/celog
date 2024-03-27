package dev.sijunyang.celog.api.error;

import dev.sijunyang.celog.core.global.error.BadRequestException;
import dev.sijunyang.celog.core.global.error.ConflictException;
import dev.sijunyang.celog.core.global.error.ForbiddenException;
import dev.sijunyang.celog.core.global.error.NotFoundException;
import dev.sijunyang.celog.core.global.error.UnauthenticatedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * RESTful API에서 발생하는 예외 상황을 처리하기 위한 전역 예외 핸들러 클래스입니다.
 *
 * @author Sijun Yang
 */
@ControllerAdvice
public class RestExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    /**
     * 예상치 못한 예외 발생 시 처리하는 핸들러 메서드입니다. 500 Internal Server Error 응답을 반환합니다.
     * @param ex 발생한 예외 객체
     * @return 예외 정보를 담은 ProblemDetail 객체
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "알수없는 문제가 발생하였습니다.");
        response.setTitle("Un_Catched");
        // TODO response.setType(URI.create());
        return response;
    }

    /**
     * {@link BadRequestException} 발생 시 처리하는 핸들러 메서드입니다. 400 Bad Request 응답을 반환합니다.
     * @param ex 발생한 예외 객체
     * @return 예외 정보를 담은 ProblemDetail 객체
     */
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleException(BadRequestException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        response.setTitle(ex.getTitle());
        // TODO response.setType(URI.create());
        return response;
    }

    /**
     * {@link ConflictException} 발생 시 처리하는 핸들러 메서드입니다. 409 Conflict 응답을 반환합니다.
     * @param ex 발생한 예외 객체
     * @return 예외 정보를 담은 ProblemDetail 객체
     */
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleException(ConflictException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getLocalizedMessage());
        response.setTitle(ex.getTitle());
        // TODO response.setType(URI.create());
        return response;
    }

    /**
     * {@link NotFoundException} 발생 시 처리하는 핸들러 메서드입니다. 404 Not Found 응답을 반환합니다.
     * @param ex 발생한 예외 객체
     * @return 예외 정보를 담은 ProblemDetail 객체
     */
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleException(NotFoundException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        response.setTitle(ex.getTitle());
        // TODO response.setType(URI.create());
        return response;
    }

    /**
     * {@link UnauthenticatedException} 발생 시 처리하는 핸들러 메서드입니다. 401 Unauthorized 응답을 반환합니다.
     * @param ex 발생한 예외 객체
     * @return 예외 정보를 담은 ProblemDetail 객체
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ProblemDetail handleException(UnauthenticatedException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage());
        response.setTitle(ex.getTitle());
        // TODO response.setType(URI.create());
        return response;
    }

    /**
     * {@link ForbiddenException} 발생 시 처리하는 핸들러 메서드입니다. 403 Forbidden 응답을 반환합니다.
     * @param ex 발생한 예외 객체
     * @return 예외 정보를 담은 ProblemDetail 객체
     */
    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleException(ForbiddenException ex) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getLocalizedMessage());
        response.setTitle(ex.getTitle());
        // TODO response.setType(URI.create());
        return response;
    }

}
