package it.unicam.cs.mpgc.rpg122423.dto;

public record EnemyDTO(
        String name,
        int currentHp,
        int maxHp,
        String intentDescription
) {}