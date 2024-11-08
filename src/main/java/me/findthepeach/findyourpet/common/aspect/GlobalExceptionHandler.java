package me.findthepeach.findyourpet.common.aspect;


import lombok.extern.slf4j.Slf4j;
import me.findthepeach.findyourpet.common.constant.domain.ResultData;
import me.findthepeach.findyourpet.common.constant.template.ReturnCode;
import me.findthepeach.findyourpet.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        log.info("GlobalExceptionHandler initialized");
    }
    /**
     * process custom BaseException
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResultData<?>> handleBaseException(BaseException ex) {
        log.info("GlobalExceptionHandler initialized");

        HttpStatus status = switch (ex.getReturnCode()) {
            case RC400 -> HttpStatus.BAD_REQUEST;


            case RC500 -> HttpStatus.INTERNAL_SERVER_ERROR;


            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        // use ResultData to construct ResponseEntity
        ResultData<?> result = ResultData.fail(ex.getReturnCode().getCode(), ex.getMessage());

        return new ResponseEntity<>(result, status);
    }

    /**
     * process other exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultData<?>> handleException(Exception ex) {
        log.error("Unhandled exception: ", ex);

        // use ResultData to construct ResponseEntity
        ResultData<?> result = ResultData.fail(ReturnCode.RC500.getCode(), "Internal Server Error");

        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultData<?>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: ", ex);
        ResultData<?> result = ResultData.fail(ReturnCode.RC401.getCode(), "Authentication failed");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultData<?>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: ", ex);
        ResultData<?> result = ResultData.fail(ReturnCode.RC403.getCode(), "Access denied");
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }
}