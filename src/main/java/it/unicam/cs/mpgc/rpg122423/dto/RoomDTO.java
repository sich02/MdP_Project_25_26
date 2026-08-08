package it.unicam.cs.mpgc.rpg122423.dto;

import java.util.List;

public record RoomDTO(
        DoorDTO north,
        DoorDTO south,
        DoorDTO east,
        DoorDTO west,
        List<EnemyDTO> enemies,
        String combatPhase
) {}