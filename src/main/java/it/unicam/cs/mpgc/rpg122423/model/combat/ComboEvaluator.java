package it.unicam.cs.mpgc.rpg122423.model.combat;

import java.util.ArrayList;
import java.util.List;

public class ComboEvaluator {

    /**
     * Valuta un array di dadi e restituisce il nome della combo, i danni totali,
     * e gli indici dei dadi che formano la combo.
     * 
     * @param dice Array di interi rappresentante i valori dei dadi
     * @return ComboResult contenente il nome della mossa, il danno calcolato e gli
     *         indici combo
     */
    public static ComboResult evaluate(int[] dice) {
        int sum = 0;
        int[] counts = new int[7];

        for (int d : dice) {
            sum += d;
            counts[d]++;
        }

        boolean has5 = false, has4 = false, has3 = false;
        int pairs = 0;
        int fiveValue = 0, fourValue = 0, threeValue = 0;
        int[] pairValues = new int[2];
        int pairIdx = 0;

        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 5) {
                has5 = true;
                fiveValue = i;
            } else if (counts[i] == 4) {
                has4 = true;
                fourValue = i;
            } else if (counts[i] == 3) {
                has3 = true;
                threeValue = i;
            } else if (counts[i] == 2) {
                if (pairIdx < 2) {
                    pairValues[pairIdx] = i;
                }
                pairIdx++;
                pairs++;
            }
        }

        boolean isStraight = (counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1
                && counts[5] == 1) ||
                (counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1 && counts[6] == 1);

        if (has5)
            return new ComboResult("CINQUINA!", sum + 20, findIndicesWithValue(dice, fiveValue));
        if (isStraight)
            return new ComboResult("SCALA!", sum + 15, allIndices(dice));
        if (has4)
            return new ComboResult("POKER!", sum + 10, findIndicesWithValue(dice, fourValue));
        if (has3 && pairs == 1)
            return new ComboResult("FULL HOUSE!", sum + 15, allIndices(dice));
        if (has3)
            return new ComboResult("TRIS", sum + 5, findIndicesWithValue(dice, threeValue));
        if (pairs == 2) {
            List<Integer> indices = findIndicesWithValue(dice, pairValues[0]);
            indices.addAll(findIndicesWithValue(dice, pairValues[1]));
            return new ComboResult("DOPPIA COPPIA", sum + 2, indices);
        }
        if (pairs == 1)
            return new ComboResult("COPPIA", sum, findIndicesWithValue(dice, pairValues[0]));

        return new ComboResult("Dado Più Alto", sum, findHighestDieIndex(dice));
    }

    /** Restituisce tutti gli indici del dado che hanno il valore specificato. */
    private static List<Integer> findIndicesWithValue(int[] dice, int value) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < dice.length; i++) {
            if (dice[i] == value) {
                indices.add(i);
            }
        }
        return indices;
    }

    /** Restituisce tutti gli indici (usato per Scala e Full House). */
    private static List<Integer> allIndices(int[] dice) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < dice.length; i++) {
            indices.add(i);
        }
        return indices;
    }

    /** Restituisce l'indice del dado con il valore più alto. */
    private static List<Integer> findHighestDieIndex(int[] dice) {
        int maxVal = 0;
        int maxIdx = 0;
        for (int i = 0; i < dice.length; i++) {
            if (dice[i] > maxVal) {
                maxVal = dice[i];
                maxIdx = i;
            }
        }
        return List.of(maxIdx);
    }
}