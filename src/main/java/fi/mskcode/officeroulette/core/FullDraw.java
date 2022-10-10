package fi.mskcode.officeroulette.core;

import java.util.List;
import java.util.Optional;

public record FullDraw(Draw draw, List<Employee> participants, Optional<DrawResult> result) {}
