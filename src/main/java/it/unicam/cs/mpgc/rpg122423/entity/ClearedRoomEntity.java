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

    @Column(nullable = false)
    private boolean lootClaimed;

    @Column(nullable = true)
    private String shopBoughtData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "save_game_id")
    private SaveGame saveGame;

    public ClearedRoomEntity() {}

    public ClearedRoomEntity(int x, int y, boolean lootClaimed, String shopBoughtData) {
        this.x = x;
        this.y = y;
        this.lootClaimed = lootClaimed;
        this.shopBoughtData = shopBoughtData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public boolean isLootClaimed() { return lootClaimed; }
    public void setLootClaimed(boolean lootClaimed) { this.lootClaimed = lootClaimed; }
    
    public String getShopBoughtData() { return shopBoughtData; }
    public void setShopBoughtData(String shopBoughtData) { this.shopBoughtData = shopBoughtData; }

    public SaveGame getSaveGame() { return saveGame; }
    public void setSaveGame(SaveGame saveGame) { this.saveGame = saveGame; }
}
