package fi.mskcode.officeroulette.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import fi.mskcode.officeroulette.core.FullDraw;
import java.util.List;

public record FullDrawResponseDto(
        long id, String status, List<EmployeeResponseDto> participants, DrawResultResponseDto result) {

    public static FullDrawResponseDto from(FullDraw draw) {
        return new FullDrawResponseDto(
                draw.draw().id(),
                draw.draw().status().name(),
                draw.participants().stream().map(EmployeeResponseDto::from).collect(toImmutableList()),
                draw.result().map(DrawResultResponseDto::from).orElse(null));
    }
}
