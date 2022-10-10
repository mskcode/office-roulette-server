package fi.mskcode.officeroulette.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import fi.mskcode.officeroulette.core.Draw;
import java.util.List;

public record DrawsResponseDto(List<DrawResponseDto> draws) {

    public static DrawsResponseDto from(List<Draw> draws) {
        var list = draws.stream().map(DrawResponseDto::from).collect(toImmutableList());
        return new DrawsResponseDto(list);
    }
}
