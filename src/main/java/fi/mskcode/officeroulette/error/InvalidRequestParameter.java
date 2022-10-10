package fi.mskcode.officeroulette.error;

public class InvalidRequestParameter extends OfficeRouletteException {

    public InvalidRequestParameter(String message) {
        super(ErrorCode.INVALID_REQUEST_PARAMETER, message, null);
    }
}
