package it.unicam.cs.mpgc.rpg122423.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "save_game")
public class SaveGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int currentFloorNumber;

    @Column(nullable = false)
    private int currentX;

    @Column(nullable = false)
    private int currentY;

    @Column(nullable = false)
    private long seed;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private PlayerEntity player;

    @OneToMany(mappedBy = "saveGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClearedRoomEntity> clearedRooms = new ArrayList<>();

    public SaveGame() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCurrentFloorNumber() { return currentFloorNumber; }
    public void setCurrentFloorNumber(int currentFloorNumber) { this.currentFloorNumber = currentFloorNumber; }

    public int getCurrentX() { return currentX; }
    public void setCurrentX(int currentX) { this.currentX = currentX; }

    public int getCurrentY() { return currentY; }
    public void setCurrentY(int currentY) { this.currentY = currentY; }

    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }

    public PlayerEntity getPlayer() { return player; }
    public void setPlayer(PlayerEntity player) { this.player = player; }

    public List<ClearedRoomEntity> getClearedRooms() { return clearedRooms; }
    public void setClearedRooms(List<ClearedRoomEntity> clearedRooms) { this.clearedRooms = clearedRooms; }

    @Column(nullable = true)
    private String lastEntryDirection;

    @OneToMany(mappedBy = "saveGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SavedEnemyEntity> savedEnemies = new ArrayList<>();

    public void addClearedRoom(ClearedRoomEntity room) {
        clearedRooms.add(room);
        room.setSaveGame(this);
    }
    
    public String getLastEntryDirection() { return lastEntryDirection; }
    public void setLastEntryDirection(String lastEntryDirection) { this.lastEntryDirection = lastEntryDirection; }
    
    public List<SavedEnemyEntity> getSavedEnemies() { return savedEnemies; }
    public void setSavedEnemies(List<SavedEnemyEntity> savedEnemies) { this.savedEnemies = savedEnemies; }
    
    public void addSavedEnemy(SavedEnemyEntity enemy) {
        savedEnemies.add(enemy);
        enemy.setSaveGame(this);
    }
}
