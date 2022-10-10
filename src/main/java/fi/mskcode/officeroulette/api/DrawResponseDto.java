package fi.mskcode.officeroulette.api;

import fi.mskcode.officeroulette.core.Draw;

public record DrawResponseDto(long id, Draw.Status status) {

    public static DrawResponseDto from(Draw draw) {
        return new DrawResponseDto(draw.id(), draw.status());
    }
}
