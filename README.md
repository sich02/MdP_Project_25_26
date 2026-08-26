# DiceRogue

DiceRogue è un roguelite a turni sviluppato in Java

Il giocatore affronta una *run* attraverso un dungeon generato proceduralmente, esplorando piani strutturati come griglie di stanze collegate (spawn, combattimento, boss, tesoro, negozio...). 
Negli scontri il danno nasce dal lancio di un pool di dadi, valutato come una mano di poker (coppia, tris, full, poker, yahtzee...): la combo scelta determina il danno inflitto al nemico bersaglio.
Lungo la run si raccolgono oggetti ed effetti che alterano le regole di gioco, offrendo variabilità run-to-run.

Ispirazioni dichiarate: *Balatro* (combo come motore della variabilità), *Dicero* (dadi come meccanismo d'attacco), *The Binding of Isaac* (struttura del dungeon a griglia e stanze speciali).

## Requisiti

- Java 21 (gestito automaticamente tramite Gradle toolchain)
- Nessuna installazione manuale di Gradle richiesta: il progetto include il Gradle Wrapper

## Come eseguire il progetto

Clonare il repository e, dalla root del progetto, lanciare:

```bash
./gradlew run
```

Su Windows:

```bash
gradlew.bat run
```

Questo compila il progetto, scarica le dipendenze (JavaFX, Hibernate, H2) e avvia l'applicazione grafica.

### Eseguire i test

```bash
./gradlew test
```

## Stack tecnico

- **Java 21**
- **Gradle** come build system
- **JavaFX 21** per l'interfaccia grafica
- **Hibernate + H2** per la persistenza dei dati (salvataggi di partita)

## Struttura del progetto

Tutte le classi risiedono nel package `it.unicam.cs.mpgc.rpg122423`, organizzato per responsabilità:

- `model` — logica di dominio (dungeon, stanze, dadi/combo, combattimento, oggetti, effetti di stato)
- `service` — servizi applicativi (generazione del dungeon, combattimento, persistenza)
- `controller` — livello di presentazione JavaFX
- `entity` — entità Hibernate per la persistenza
- `dto` — oggetti di trasferimento dati tra dominio e UI

## Uso di strumenti di intelligenza artificiale

Nello sviluppo di questo progetto è stato utilizzato **Claude (Anthropic)** come assistente per:

- l'analisi dei requisiti e la progettazione del modello di dominio;
- la revisione del codice rispetto ai principi SOLID e alla ricerca di violazioni delle invarianti architetturali (es. generazione del dungeon);
- il supporto nella scrittura e nel debug di parti del codice.

Le decisioni architetturali e di design sono state prese e validate dallo studente; l'assistente AI è stato impiegato come supporto al processo di sviluppo, non come sostituto della progettazione.
