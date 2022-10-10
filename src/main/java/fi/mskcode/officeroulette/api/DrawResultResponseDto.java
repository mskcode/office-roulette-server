package fi.mskcode.officeroulette.api;

import fi.mskcode.officeroulette.core.DrawResult;
import fi.mskcode.officeroulette.error.NotImplementedException;

public record DrawResultResponseDto() {

    public static DrawResultResponseDto from(DrawResult drawResult) {
        throw new NotImplementedException();
    }
}
