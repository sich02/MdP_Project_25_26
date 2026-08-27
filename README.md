# DiceRogue

DiceRogue è un roguelite a turni sviluppato in Java con interfaccia grafica JavaFX. Il giocatore esplora un dungeon generato proceduralmente su più piani e affronta combattimenti basati sul lancio di un pool di dadi, valutato come una mano di poker per determinare il danno inflitto ai nemici. 



## Come eseguire il progetto

### Prerequisiti
- Java 21 
- Gradle (incluso tramite Gradle Wrapper, non serve installarlo separatamente)

### Istruzioni

```bash
git clone https://github.com/sich02/MdP_Project_25_26.git
cd MdP_Project_25_26
```

### Build del progetto
```bash
./gradlew build
```

### Esecuzione
```bash
./gradlew run
```

Su Windows, sostituire `./gradlew` con `gradlew.bat` in entrambi i comandi.

---

## Uso di strumenti di AI

Utilizzato Claude (Anthropic) come supporto durante lo sviluppo per:

* Chiarire errori di compilazione e problemi di configurazione Gradle
* Suggerimenti sulla struttura dei package e delle classi
* Generazione di una prima versione di alcune classi, poi analizzata, compresa e adattata manualmente
* Supporto nella scrittura dei test JUnit

Per una descrizione più dettagliata dell'uso dell'AI, si veda la Wiki del repository.
