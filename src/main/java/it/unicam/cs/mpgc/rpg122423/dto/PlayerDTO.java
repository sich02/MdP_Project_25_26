package it.unicam.cs.mpgc.rpg122423.dto;

public record PlayerDTO(
        double currentHearts,
        double maxHearts,
        int gold,
        int keys
) {}