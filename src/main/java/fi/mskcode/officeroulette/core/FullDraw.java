package fi.mskcode.officeroulette.core;

import java.util.List;

public record FullDraw(Draw draw, List<Employee> participants, DrawResult result) {}
