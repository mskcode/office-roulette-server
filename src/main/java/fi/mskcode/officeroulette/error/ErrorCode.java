package fi.mskcode.officeroulette.error;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST),
    NOT_IMPLEMENTED(HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    UNSPECIFIED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    public final HttpStatus responseStatus;

    ErrorCode(HttpStatus responseStatus) {
        this.responseStatus = notNull(responseStatus);
    }
}
