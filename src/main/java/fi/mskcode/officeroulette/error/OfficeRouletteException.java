package fi.mskcode.officeroulette.error;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public abstract class OfficeRouletteException extends RuntimeException {

    public final ErrorCode code;

    protected OfficeRouletteException(ErrorCode code, String message, Throwable cause) {
        super(notBlank(message), cause);
        this.code = notNull(code);
    }
}
