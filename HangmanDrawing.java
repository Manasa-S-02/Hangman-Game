package hangman;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class HangmanDrawing extends Canvas {

    private GraphicsContext gc;
    private boolean gameOver = false;

    // Colors
    private static final Color GALLOWS_COLOR = Color.web("#5a5a5a");
    private static final Color BODY_COLOR    = Color.web("#e05a5a");
    private static final Color WIN_COLOR     = Color.web("#4caf88");

    public HangmanDrawing() {
        super(280, 300);
        gc = getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        draw(0, false);
    }

    public void draw(int mistakes, boolean won) {
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawGallows();
        Color bodyColor = won ? WIN_COLOR : BODY_COLOR;

        if (mistakes >= 1) drawHead(bodyColor);
        if (mistakes >= 2) drawBody(bodyColor);
        if (mistakes >= 3) drawLeftArm(bodyColor);
        if (mistakes >= 4) drawRightArm(bodyColor);
        if (mistakes >= 5) drawLeftLeg(bodyColor);
        if (mistakes >= 6) drawRightLeg(bodyColor);

        if (won) drawSmile();
    }

    private void drawGallows() {
        gc.setStroke(GALLOWS_COLOR);
        gc.setLineWidth(5);

        // Base
        gc.strokeLine(30, 270, 200, 270);
        // Pole
        gc.strokeLine(80, 270, 80, 30);
        // Top beam
        gc.strokeLine(80, 30, 175, 30);
        // Support brace
        gc.strokeLine(80, 80, 120, 30);
        // Rope
        gc.setLineWidth(2);
        gc.strokeLine(175, 30, 175, 65);
    }

    private void drawHead(Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeOval(155, 65, 40, 40);
    }

    private void drawBody(Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(175, 105, 175, 180);
    }

    private void drawLeftArm(Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(175, 125, 145, 160);
    }

    private void drawRightArm(Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(175, 125, 205, 160);
    }

    private void drawLeftLeg(Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(175, 180, 145, 220);
    }

    private void drawRightLeg(Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(175, 180, 205, 220);
    }

    private void drawSmile() {
        gc.setStroke(WIN_COLOR);
        gc.setLineWidth(2);
        // Happy arc
        gc.strokeArc(163, 80, 24, 18, 200, -160, javafx.scene.shape.ArcType.OPEN);
        // Eyes
        gc.fillOval(163, 75, 5, 5);
        gc.fillOval(176, 75, 5, 5);
    }

    public void reset() {
        draw(0, false);
    }
}
