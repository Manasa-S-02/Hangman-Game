package hangman;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private VBox leftPanel;
    @FXML private VBox rightPanel;

    @FXML private StackPane canvasContainer;
    @FXML private Label wordLabel;
    @FXML private Label statusLabel;
    @FXML private Label mistakesLabel;
    @FXML private Label winsLabel;
    @FXML private Label lossesLabel;
    @FXML private Label winRateLabel;
    @FXML private FlowPane letterGrid;
    @FXML private Button newGameBtn;
    @FXML private Button hintBtn;
    @FXML private ComboBox<String> difficultyBox;

    private HangmanDrawing hangmanDrawing;
    private WordManager wordManager;
    private Map<Character, Button> letterButtons;
    private int wins = 0;
    private int losses = 0;
    private boolean gameActive = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wordManager = new WordManager();
        letterButtons = new HashMap<>();

        // Attach canvas
        hangmanDrawing = new HangmanDrawing();
        canvasContainer.getChildren().add(hangmanDrawing);

        // Build A-Z letter buttons
        buildLetterGrid();

        // Difficulty combo
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setValue("Medium");
        difficultyBox.setOnAction(e -> {
            switch (difficultyBox.getValue()) {
                case "Easy"   -> wordManager.setDifficulty(WordManager.Difficulty.EASY);
                case "Medium" -> wordManager.setDifficulty(WordManager.Difficulty.MEDIUM);
                case "Hard"   -> wordManager.setDifficulty(WordManager.Difficulty.HARD);
            }
        });

        updateScoreBoard();
        startNewGame();
    }

    private void buildLetterGrid() {
        letterGrid.getChildren().clear();
        letterButtons.clear();
        for (char c = 'A'; c <= 'Z'; c++) {
            Button btn = new Button(String.valueOf(c));
            btn.getStyleClass().add("letter-btn");
            btn.setPrefWidth(42);
            btn.setPrefHeight(42);
            char letter = c;
            btn.setOnAction(e -> handleGuess(letter));
            letterButtons.put(c, btn);
            letterGrid.getChildren().add(btn);
        }
    }

    private void startNewGame() {
        wordManager.newGame();
        gameActive = true;

        // Reset all letter buttons
        for (Button btn : letterButtons.values()) {
            btn.getStyleClass().removeAll("correct", "wrong");
            btn.setDisable(false);
        }

        hangmanDrawing.reset();
        wordLabel.setText(wordManager.getDisplayWord());
        statusLabel.setText("Guess a letter!");
        statusLabel.getStyleClass().removeAll("status-win", "status-loss", "status-warn");
        statusLabel.getStyleClass().add("status-neutral");
        mistakesLabel.setText("Mistakes: 0 / 6");
        hintBtn.setDisable(false);

        animateFadeIn(wordLabel);
    }

    private void handleGuess(char letter) {
        if (!gameActive) return;
        if (wordManager.hasBeenGuessed(letter)) return;

        boolean correct = wordManager.guess(letter);
        Button btn = letterButtons.get(letter);

        if (correct) {
            btn.getStyleClass().add("correct");
            btn.setDisable(true);
            animateBounce(btn);
            statusLabel.getStyleClass().removeAll("status-win", "status-loss", "status-warn", "status-neutral");
            statusLabel.getStyleClass().add("status-neutral");
            statusLabel.setText("Nice! '" + letter + "' is in the word.");
        } else {
            btn.getStyleClass().add("wrong");
            btn.setDisable(true);
            statusLabel.getStyleClass().removeAll("status-win", "status-loss", "status-warn", "status-neutral");
            statusLabel.getStyleClass().add("status-warn");
            statusLabel.setText("Wrong! '" + letter + "' is not in the word.");
        }

        int mistakes = wordManager.getMistakes();
        mistakesLabel.setText("Mistakes: " + mistakes + " / 6");
        hangmanDrawing.draw(mistakes, false);
        wordLabel.setText(wordManager.getDisplayWord());

        if (wordManager.isWon()) {
            endGame(true);
        } else if (wordManager.isLost()) {
            endGame(false);
        }
    }

    private void endGame(boolean won) {
        gameActive = false;
        hintBtn.setDisable(true);

        // Disable all remaining buttons
        for (Button btn : letterButtons.values()) {
            btn.setDisable(true);
        }

        if (won) {
            wins++;
            hangmanDrawing.draw(wordManager.getMistakes(), true);
            statusLabel.getStyleClass().removeAll("status-win", "status-loss", "status-warn", "status-neutral");
            statusLabel.getStyleClass().add("status-win");
            statusLabel.setText("You won! The word was: " + wordManager.getCurrentWord());
            wordLabel.setText(wordManager.getCurrentWord().chars()
                    .mapToObj(c -> String.valueOf((char) c))
                    .reduce("", (a, b) -> a + b + " ").trim());
        } else {
            losses++;
            statusLabel.getStyleClass().removeAll("status-win", "status-loss", "status-warn", "status-neutral");
            statusLabel.getStyleClass().add("status-loss");
            statusLabel.setText("Game over! The word was: " + wordManager.getCurrentWord());
            // Reveal the full word
            wordLabel.setText(wordManager.getCurrentWord().chars()
                    .mapToObj(c -> String.valueOf((char) c))
                    .reduce("", (a, b) -> a + b + " ").trim());
        }

        updateScoreBoard();
        animateFadeIn(statusLabel);
    }

    @FXML
    private void onNewGame() {
        startNewGame();
    }

    @FXML
    private void onHint() {
        if (!gameActive) return;
        String hint = wordManager.getHint();
        if (hint != null) {
            char letter = hint.charAt(0);
            Button btn = letterButtons.get(letter);
            if (btn != null) {
                btn.getStyleClass().add("correct");
                btn.setDisable(true);
            }
            wordLabel.setText(wordManager.getDisplayWord());
            statusLabel.getStyleClass().removeAll("status-win", "status-loss", "status-warn", "status-neutral");
            statusLabel.getStyleClass().add("status-neutral");
            statusLabel.setText("Hint: '" + letter + "' has been revealed.");

            if (wordManager.isWon()) endGame(true);
        }
    }

    public void handleKeyPress(char letter) {
        handleGuess(Character.toUpperCase(letter));
    }

    private void updateScoreBoard() {
        winsLabel.setText(String.valueOf(wins));
        lossesLabel.setText(String.valueOf(losses));
        int total = wins + losses;
        int rate = total == 0 ? 0 : (int) Math.round((wins * 100.0) / total);
        winRateLabel.setText(rate + "%");
    }

    private void animateFadeIn(javafx.scene.Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setFromValue(0.2);
        ft.setToValue(1.0);
        ft.play();
    }

    private void animateBounce(javafx.scene.Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.3);
        st.setToY(1.3);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }
}
