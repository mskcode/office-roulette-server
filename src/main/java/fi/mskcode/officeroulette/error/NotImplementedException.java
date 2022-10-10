package fi.mskcode.officeroulette.error;

public class NotImplementedException extends OfficeRouletteException {

    public NotImplementedException() {
        super(ErrorCode.NOT_IMPLEMENTED, "Implementation missing", null);
    }
}
