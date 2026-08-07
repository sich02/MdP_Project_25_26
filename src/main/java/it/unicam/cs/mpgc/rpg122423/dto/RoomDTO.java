package it.unicam.cs.mpgc.rpg122423.dto;

public record RoomDTO(
        boolean hasNorth,
        boolean hasSouth,
        boolean hasEast,
        boolean hasWest
) {}