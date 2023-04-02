package fi.mskcode.officeroulette.api;

import static fi.mskcode.officeroulette.util.DateTimeUtil.parseIso8601String;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

record CreateNewEmployeeRequestDto(String firstName, String lastName, Instant employmentStartTime) {

    @JsonCreator
    public CreateNewEmployeeRequestDto(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("employmentStartTime") String employmentStartTime) {
        this(firstName, lastName, parseIso8601String(employmentStartTime));
    }
}
