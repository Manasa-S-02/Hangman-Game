package hangman;

import java.util.*;

public class WordManager {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private static final Map<Difficulty, String[]> WORD_BANK = new HashMap<>();

    static {
        WORD_BANK.put(Difficulty.EASY, new String[]{
            "CAT", "DOG", "SUN", "HAT", "MAP", "PEN", "CUP", "BOX",
            "JAR", "KEY", "LEG", "NET", "OAK", "POT", "RAG", "SEA"
        });
        WORD_BANK.put(Difficulty.MEDIUM, new String[]{
            "JAVAFX", "CODING", "PLANET", "BRIDGE", "CASTLE", "DRAGON",
            "FALCON", "GARDEN", "HELMET", "ISLAND", "JUNGLE", "KNIGHT",
            "LANTERN", "MIRROR", "NEEDLE", "ORANGE", "PARROT", "QUARTZ"
        });
        WORD_BANK.put(Difficulty.HARD, new String[]{
            "POLYMORPHISM", "ENCAPSULATION", "INHERITANCE", "ABSTRACTION",
            "SYNCHRONIZE", "QUARTERBACK", "CRYPTOGRAPHY", "PHOTOSYNTHESIS",
            "ARCHAEOLOGY", "BUREAUCRACY", "CHRYSANTHEMUM", "KALEIDOSCOPE"
        });
    }

    private String currentWord;
    private Set<Character> guessedLetters;
    private int mistakes;
    private Difficulty difficulty;

    public static final int MAX_MISTAKES = 6;

    public WordManager() {
        this.difficulty = Difficulty.MEDIUM;
        this.guessedLetters = new HashSet<>();
    }

    public void newGame() {
        String[] words = WORD_BANK.get(difficulty);
        currentWord = words[new Random().nextInt(words.length)].toUpperCase();
        guessedLetters.clear();
        mistakes = 0;
    }

    public boolean guess(char letter) {
        letter = Character.toUpperCase(letter);
        if (guessedLetters.contains(letter)) return false;
        guessedLetters.add(letter);
        boolean correct = currentWord.indexOf(letter) >= 0;
        if (!correct) mistakes++;
        return correct;
    }

    public boolean hasBeenGuessed(char letter) {
        return guessedLetters.contains(Character.toUpperCase(letter));
    }

    public String getDisplayWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            sb.append(guessedLetters.contains(c) ? c : '_');
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public boolean isWon() {
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) return false;
        }
        return true;
    }

    public boolean isLost() {
        return mistakes >= MAX_MISTAKES;
    }

    public int getMistakes() {
        return mistakes;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getHint() {
        // Reveal a random unguessed letter
        List<Character> unguessed = new ArrayList<>();
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c) && !unguessed.contains(c)) {
                unguessed.add(c);
            }
        }
        if (unguessed.isEmpty()) return null;
        char hint = unguessed.get(new Random().nextInt(unguessed.size()));
        guess(hint);
        return String.valueOf(hint);
    }
}
