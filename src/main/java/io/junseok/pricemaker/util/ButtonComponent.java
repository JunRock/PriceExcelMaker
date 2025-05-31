package io.junseok.pricemaker.util;

import javafx.scene.control.Button;

public class ButtonComponent {
    public static Button createButton(String function){
        return new Button(function);
    }

    private ButtonComponent() {
    }
}
