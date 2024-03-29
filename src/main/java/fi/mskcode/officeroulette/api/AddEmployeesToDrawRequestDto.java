package fi.mskcode.officeroulette.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

// couldn't make a record out of this since Java's type-erasure makes List<String> and List<UUID> in method signatures
// ambiguous
public class AddEmployeesToDrawRequestDto {

    public final long drawId;
    public final List<UUID> participants;

    @JsonCreator
    public AddEmployeesToDrawRequestDto(
            @JsonProperty("drawId") long drawId, @JsonProperty("participants") List<String> participants) {
        this.drawId = drawId;
        this.participants = participants.stream().map(UUID::fromString).collect(toImmutableList());
    }
}
