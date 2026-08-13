package it.unicam.cs.mpgc.rpg122423.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "saved_enemy")
public class SavedEnemyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int currentHp;

    @Column(nullable = false)
    private int maxHp;
    
    @Column(nullable = false)
    private int baseDmg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "save_game_id")
    private SaveGame saveGame;

    public SavedEnemyEntity() {}

    public SavedEnemyEntity(String name, int currentHp, int maxHp, int baseDmg) {
        this.name = name;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.baseDmg = baseDmg;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    
    public int getBaseDmg() { return baseDmg; }
    public void setBaseDmg(int baseDmg) { this.baseDmg = baseDmg; }

    public SaveGame getSaveGame() { return saveGame; }
    public void setSaveGame(SaveGame saveGame) { this.saveGame = saveGame; }
}
