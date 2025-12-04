package com.comp2042.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    // Panel dimensions
    /** Minimum height of the notification panel in pixels */
    private static final double PANEL_MIN_HEIGHT = 200.0;
    
    /** Minimum width of the notification panel in pixels */
    private static final double PANEL_MIN_WIDTH = 220.0;
    
    // Visual effects
    /** Glow effect intensity level (0.0 to 1.0) */
    private static final double GLOW_EFFECT_LEVEL = 0.6;
    
    // Animation timing
    /** Duration of fade transition in milliseconds */
    private static final int FADE_TRANSITION_DURATION_MS = 2000;
    
    /** Duration of translate transition in milliseconds */
    private static final int TRANSLATE_TRANSITION_DURATION_MS = 2500;
    
    /** Vertical offset for upward translation animation in pixels */
    private static final double TRANSLATE_UPWARD_OFFSET = 40.0;
    
    // Opacity values for fade animation
    /** Starting opacity for fade transition (fully opaque) */
    private static final double FADE_START_OPACITY = 1.0;
    
    /** Ending opacity for fade transition (fully transparent) */
    private static final double FADE_END_OPACITY = 0.0;

    public NotificationPanel(String text) {
        setMinHeight(PANEL_MIN_HEIGHT);
        setMinWidth(PANEL_MIN_WIDTH);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(GLOW_EFFECT_LEVEL);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(FADE_TRANSITION_DURATION_MS), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(TRANSLATE_TRANSITION_DURATION_MS), this);
        tt.setToY(this.getLayoutY() - TRANSLATE_UPWARD_OFFSET);
        ft.setFromValue(FADE_START_OPACITY);
        ft.setToValue(FADE_END_OPACITY);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}

