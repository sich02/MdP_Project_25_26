package it.unicam.cs.mpgc.rpg122423.dto;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.RoomType;

public record DoorDTO(boolean exists, RoomType roomType, boolean isLocked) {}