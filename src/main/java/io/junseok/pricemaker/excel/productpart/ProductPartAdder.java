package io.junseok.pricemaker.excel.productpart;


import static io.junseok.pricemaker.util.ButtonComponent.createButton;
import static io.junseok.pricemaker.util.Text.createTextField;

import java.text.DecimalFormat;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProductPartAdder {
    public static final DecimalFormat priceFormat = new DecimalFormat("#,###.##");

    public void addPartRow(VBox container) {
        TextField partNameField = createTextField("부품 이름", 200);
        TextField costField = createTextField("원가(₩)", 100);

        costField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.matches("[0-9.]")) {
                event.consume();
            }
        });

        costField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    String input = costField.getText().replace(",", "").trim();
                    if (!input.isEmpty()) {
                        double value = Double.parseDouble(input);
                        costField.setText(priceFormat.format(value));
                    }
                } catch (NumberFormatException e) {
                    costField.setText("");
                }
            }
        });

        Button removeBtn = createButton("삭제");
        HBox row = new HBox(10, partNameField, costField, removeBtn);
        row.setPadding(new Insets(5));
        removeBtn.setOnAction(e -> container.getChildren().remove(row));

        container.getChildren().add(row);
        partNameField.setOnAction(e -> costField.requestFocus());
    }
}
