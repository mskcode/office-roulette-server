package fi.mskcode.officeroulette.api;

import static fi.mskcode.officeroulette.util.DateTimeUtil.formatAsIso8601;

import fi.mskcode.officeroulette.core.DrawResult;

public record DrawResultResponseDto(String winnerEmployeeId, String resultTime) {

    public static DrawResultResponseDto from(DrawResult drawResult) {
        return new DrawResultResponseDto(
                drawResult.winnerEmployeeId().toString(), formatAsIso8601(drawResult.resultInsertTime()));
    }
}
