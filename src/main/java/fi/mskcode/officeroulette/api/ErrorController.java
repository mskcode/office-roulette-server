package fi.mskcode.officeroulette.api;

import static java.lang.String.format;

import fi.mskcode.officeroulette.error.ErrorCode;
import fi.mskcode.officeroulette.error.OfficeRouletteException;
import java.lang.invoke.MethodHandles;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {

    private static final ErrorCode THROWABLE_ERROR_CODE = ErrorCode.UNSPECIFIED_ERROR;

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    record ErrorDto(String code, String message) {}

    @ExceptionHandler(OfficeRouletteException.class)
    public ResponseEntity<ErrorDto> officeRouletteExceptionHandler(
            OfficeRouletteException e, HttpServletRequest request) {
        logger.error("Exception while handling request: {}", formatRequestURI(request), e);
        return new ResponseEntity<>(new ErrorDto(e.code.name(), e.getMessage()), e.code.responseStatus);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> throwableExceptionHandler(Throwable e, HttpServletRequest request) {
        logger.error("Exception while handling request: {}", formatRequestURI(request), e);
        return new ResponseEntity<>(
                new ErrorDto(THROWABLE_ERROR_CODE.name(), e.getMessage()), THROWABLE_ERROR_CODE.responseStatus);
    }

    private static String formatRequestURI(HttpServletRequest request) {
        return format("%s %s", request.getMethod(), request.getRequestURI());
    }
}
