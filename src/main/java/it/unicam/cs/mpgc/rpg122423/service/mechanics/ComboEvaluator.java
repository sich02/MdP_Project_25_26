package it.unicam.cs.mpgc.rpg122423.service.mechanics;

import it.unicam.cs.mpgc.rpg122423.dto.Combo;
import it.unicam.cs.mpgc.rpg122423.dto.RollResult;
import it.unicam.cs.mpgc.rpg122423.model.dice.ComboType;

import java.util.*;
import java.util.stream.Collectors;

public class ComboEvaluator {

    public List<Combo> evaluate(RollResult result) {
        Map<Integer, Long> frequencies = result.values().stream()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));

        List<Combo> combos = new ArrayList<>();
        findNOfAKind(frequencies, 5, ComboType.YAHTZEE).ifPresent(combos::add);
        findNOfAKind(frequencies, 4, ComboType.FOUR_OF_A_KIND).ifPresent(combos::add);
        findFullHouse(frequencies).ifPresent(combos::add);
        findStraight(result.values()).ifPresent(combos::add);
        findNOfAKind(frequencies, 3, ComboType.THREE_OF_A_KIND).ifPresent(combos::add);
        findTwoPair(frequencies).ifPresent(combos::add);
        findNOfAKind(frequencies, 2, ComboType.PAIR).ifPresent(combos::add);
        combos.add(new Combo(ComboType.HIGH_CARD, List.of(Collections.max(result.values())), ComboType.HIGH_CARD.getBaseDamage()));

        combos.sort((c1, c2) -> Integer.compare(c2.totalDamage(), c1.totalDamage()));
        return combos;
    }

    private Optional<Combo> findNOfAKind(Map<Integer, Long> frequencies, int n, ComboType type) {
        return frequencies.entrySet().stream()
                .filter(e -> e.getValue() >= n)
                .map(e -> new Combo(type, Collections.nCopies(n, e.getKey()), type.getBaseDamage()))
                .findFirst();
    }

    private Optional<Combo> findFullHouse(Map<Integer, Long> frequencies) {
        int trisVal = frequencies.entrySet().stream().filter(e -> e.getValue() >= 3).map(Map.Entry::getKey).findFirst().orElse(0);
        int pairVal = frequencies.entrySet().stream().filter(e -> e.getValue() >= 2 && e.getKey() != trisVal).map(Map.Entry::getKey).findFirst().orElse(0);

        if (trisVal != 0 && pairVal != 0) {
            List<Integer> values = new ArrayList<>();
            values.addAll(Collections.nCopies(3, trisVal));
            values.addAll(Collections.nCopies(2, pairVal));
            return Optional.of(new Combo(ComboType.FULL_HOUSE, values, ComboType.FULL_HOUSE.getBaseDamage()));
        }
        return Optional.empty();
    }

    private Optional<Combo> findTwoPair(Map<Integer, Long> frequencies) {
        List<Integer> pairs = frequencies.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .limit(2)
                .toList();

        if (pairs.size() == 2) {
            List<Integer> values = new ArrayList<>();
            values.addAll(Collections.nCopies(2, pairs.get(0)));
            values.addAll(Collections.nCopies(2, pairs.get(1)));
            return Optional.of(new Combo(ComboType.TWO_PAIR, values, ComboType.TWO_PAIR.getBaseDamage()));
        }
        return Optional.empty();
    }

    private Optional<Combo> findStraight(List<Integer> values) {
        List<Integer> distinctSorted = values.stream().distinct().sorted().toList();
        if (distinctSorted.size() == 5 && (distinctSorted.get(4) - distinctSorted.get(0) == 4)) {
            return Optional.of(new Combo(ComboType.STRAIGHT, distinctSorted, ComboType.STRAIGHT.getBaseDamage()));
        }
        return Optional.empty();
    }
}