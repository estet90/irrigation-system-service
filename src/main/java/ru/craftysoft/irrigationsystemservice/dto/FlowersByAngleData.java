package ru.craftysoft.irrigationsystemservice.dto;

import java.util.HashSet;
import java.util.Set;

public class FlowersByAngleData {
    private final Set<Double> distances = new HashSet<>();
    private final Set<String> flowersNames = new HashSet<>();

    public Set<Double> getDistances() {
        return distances;
    }

    public Set<String> getFlowersNames() {
        return flowersNames;
    }
}
