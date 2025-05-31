package io.junseok.pricemaker;

import javafx.scene.layout.VBox;

public class MainController {
    private final VBox productListContainer = new VBox(10);

    public VBox getProductListContainer() {
        return productListContainer;
    }
}