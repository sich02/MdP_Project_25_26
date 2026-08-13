package it.unicam.cs.mpgc.rpg122423.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "player_state")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int currentHp;

    @Column(nullable = false)
    private int maxHp;

    @Column(nullable = false)
    private int gold;

    @Column(nullable = false)
    private int keys;

    @Column(nullable = false)
    private int bonusDamage;

    public PlayerEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getKeys() { return keys; }
    public void setKeys(int keys) { this.keys = keys; }

    public int getBonusDamage() { return bonusDamage; }
    public void setBonusDamage(int bonusDamage) { this.bonusDamage = bonusDamage; }
}
