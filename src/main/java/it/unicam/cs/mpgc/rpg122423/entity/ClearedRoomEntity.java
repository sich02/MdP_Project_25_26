package it.unicam.cs.mpgc.rpg122423.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cleared_room")
public class ClearedRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "save_game_id")
    private SaveGame saveGame;

    public ClearedRoomEntity() {}

    public ClearedRoomEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public SaveGame getSaveGame() { return saveGame; }
    public void setSaveGame(SaveGame saveGame) { this.saveGame = saveGame; }
}
