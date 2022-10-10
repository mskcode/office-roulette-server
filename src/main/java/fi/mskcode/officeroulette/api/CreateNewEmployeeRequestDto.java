package fi.mskcode.officeroulette.api;

import static fi.mskcode.officeroulette.util.DateTimeUtil.parseIso8601String;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.Instant;

record CreateNewEmployeeRequestDto(String firstName, String lastName, Instant employmentStartTime) {

    @JsonCreator
    public CreateNewEmployeeRequestDto(String firstName, String lastName, String employmentStartTime) {
        this(firstName, lastName, parseIso8601String(employmentStartTime));
    }
}
