package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CombatRoomTest {

    @Test
    void testRoomWithoutLoot() {
        it.unicam.cs.mpgc.rpg122423.model.combat.Enemy dummy = new it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy("Dummy", 10, 1);
        CombatRoom room = new CombatRoom(false, java.util.List.of(dummy), null);

        assertFalse(room.isCleared(), "La CombatRoom non deve essere risolta appena creata.");
        assertFalse(room.hasLoot(), "Una stanza generata senza loot non deve mai averlo, anche all'inizio.");

        room.markAsCleared();

        assertTrue(room.isCleared(), "La stanza deve risultare pulita dopo markAsCleared().");
        assertFalse(room.hasLoot(), "Anche dopo averla pulita, non deve esserci loot se generata con false.");
    }

    @Test
    void testRoomWithLootRequiresClearing() {
        it.unicam.cs.mpgc.rpg122423.model.combat.Enemy dummy = new it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy("Dummy", 10, 1);
        CombatRoom room = new CombatRoom(true, java.util.List.of(dummy), new it.unicam.cs.mpgc.rpg122423.model.item.CoinItem());

        assertFalse(room.hasLoot(), "Il loot non deve essere disponibile PRIMA di aver pulito la stanza.");

        room.markAsCleared();

        assertTrue(room.hasLoot(), "Il loot deve diventare disponibile DOPO aver pulito la stanza.");
    }

    @Test
    void testClaimLootIsIdempotent() {
        it.unicam.cs.mpgc.rpg122423.model.combat.Enemy dummy = new it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy("Dummy", 10, 1);
        CombatRoom room = new CombatRoom(true, java.util.List.of(dummy), new it.unicam.cs.mpgc.rpg122423.model.item.CoinItem());

        room.markAsCleared(); // Rendiamo il loot disponibile
        room.claimLoot();

        assertFalse(room.hasLoot(), "Dopo claimLoot(), non deve esserci più loot disponibile.");

        // Test per confermare che non crasha chiamandolo due volte
        assertDoesNotThrow(room::claimLoot, "Chiamare claimLoot() più volte non deve lanciare eccezioni.");
        assertFalse(room.hasLoot());
    }
}