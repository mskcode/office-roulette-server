package fi.mskcode.officeroulette.error;

public class ResourceNotFound extends OfficeRouletteException {

    public ResourceNotFound(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message, null);
    }
}
