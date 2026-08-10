package it.unicam.cs.mpgc.rpg122423.model.combat;

public class ComboEvaluator {

    /**
     * Valuta un array di dadi e restituisce il nome della combo e i danni totali.
     * @param dice Array di interi rappresentante i valori dei dadi
     * @return ComboResult contenente il nome della mossa e il danno calcolato
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

        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 5) has5 = true;
            else if (counts[i] == 4) has4 = true;
            else if (counts[i] == 3) has3 = true;
            else if (counts[i] == 2) pairs++;
        }

        boolean isStraight = (counts[1]==1 && counts[2]==1 && counts[3]==1 && counts[4]==1 && counts[5]==1) ||
                (counts[2]==1 && counts[3]==1 && counts[4]==1 && counts[5]==1 && counts[6]==1);

        if (has5) return new ComboResult("CINQUINA!", sum + 20);
        if (isStraight) return new ComboResult("SCALA!", sum + 15);
        if (has4) return new ComboResult("POKER!", sum + 10);
        if (has3 && pairs == 1) return new ComboResult("FULL HOUSE!", sum + 15);
        if (has3) return new ComboResult("TRIS", sum + 5);
        if (pairs == 2) return new ComboResult("DOPPIA COPPIA", sum + 2);
        if (pairs == 1) return new ComboResult("COPPIA", sum);

        return new ComboResult("NESSUNA COMBO", sum);
    }
}