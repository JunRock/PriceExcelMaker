package io.junseok.pricemaker.excel.product;

import static io.junseok.pricemaker.util.ButtonComponent.createButton;
import static io.junseok.pricemaker.util.Text.createTextField;

import io.junseok.pricemaker.MainController;
import io.junseok.pricemaker.excel.productpart.ProductPartAdder;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class ProductAdder {
    private final MainController controller;
    private final ProductPartAdder productPartAdder;
    public ProductAdder(MainController controller, ProductPartAdder productPartAdder) {
        this.controller = controller;
        this.productPartAdder = productPartAdder;
    }

    public void addProductRow() {
        VBox productBox = new VBox(5);
        TextField productNameField = createTextField("제품명", 300);

        productNameField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (productNameField.getText().length() > 100) {
                event.consume();
            }
        });

        VBox partListBox = new VBox(5);
        Button addPartBtn = createButton("+ 부품 추가");
        addPartBtn.setOnAction(e -> productPartAdder.addPartRow(partListBox));

        productPartAdder.addPartRow(partListBox);

        Button removeProductBtn = createButton("제품 삭제");
        removeProductBtn.setOnAction(e -> controller.getProductListContainer().getChildren().remove(productBox));

        productBox.getChildren().addAll(
            new Label("제품명"),
            productNameField,
            new Label("부품 목록"),
            partListBox,
            addPartBtn,
            removeProductBtn,
            new Separator()
        );

        controller.getProductListContainer().getChildren().add(productBox);
    }
}
