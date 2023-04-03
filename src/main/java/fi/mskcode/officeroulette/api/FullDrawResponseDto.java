package fi.mskcode.officeroulette.api;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static fi.mskcode.officeroulette.util.DateTimeUtil.formatAsIso8601;

import fi.mskcode.officeroulette.core.FullDraw;
import fi.mskcode.officeroulette.util.DateTimeUtil;
import java.util.List;
import java.util.Optional;

public record FullDrawResponseDto(
        long id,
        String status,
        String insertTime,
        Optional<String> drawTime,
        List<EmployeeResponseDto> participants,
        DrawResultResponseDto result) {

    public static FullDrawResponseDto from(FullDraw draw) {
        return new FullDrawResponseDto(
                draw.draw().id(),
                draw.draw().status().name(),
                formatAsIso8601(draw.draw().insertTime()),
                draw.draw().drawTime().map(DateTimeUtil::formatAsIso8601),
                draw.participants().stream().map(EmployeeResponseDto::from).collect(toImmutableList()),
                draw.result().map(DrawResultResponseDto::from).orElse(null));
    }
}
