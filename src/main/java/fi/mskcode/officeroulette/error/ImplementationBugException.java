package fi.mskcode.officeroulette.error;

public class ImplementationBugException extends OfficeRouletteException {

    public ImplementationBugException(String message) {
        super(ErrorCode.IMPLEMENTATION_BUG, message, null);
    }
}
