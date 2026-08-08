package it.unicam.cs.mpgc.rpg122423.dto;

public record RoomDTO(
        DoorDTO north,
        DoorDTO south,
        DoorDTO east,
        DoorDTO west
) {}