package io.junseok.pricemaker.util;

import javafx.scene.control.TextField;

public class Text {
    public static TextField createTextField(String prompt, double width) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        return field;
    }

    private Text() {
    }
}
