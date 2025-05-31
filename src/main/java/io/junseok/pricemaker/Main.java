package io.junseok.pricemaker;

import static io.junseok.pricemaker.util.ButtonComponent.createButton;

import io.junseok.pricemaker.excel.ExcelLoader;
import io.junseok.pricemaker.excel.ExcelSaver;
import io.junseok.pricemaker.excel.product.ProductAdder;
import io.junseok.pricemaker.excel.productpart.ProductPartAdder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private MainController controller;
    @Override
    public void start(Stage stage) {
        this.controller = new MainController();
        ExcelSaver excelSaver = new ExcelSaver(controller);
        ProductPartAdder productPartAdder = new ProductPartAdder();
        ProductAdder productAdder = new ProductAdder(controller, productPartAdder);
        ExcelLoader excelLoader = new ExcelLoader(controller, productPartAdder);
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        ScrollPane scrollPane = scrollPane();

        HBox buttonBar = new HBox(10);
        Button addProductBtn = createButton("+ 제품 추가");
        Button loadBtn = createButton("엑셀 불러오기(.xlsx)");
        Button saveBtn = createButton("엑셀 저장");

        addProductBtn.setOnAction(e -> productAdder.addProductRow());
        loadBtn.setOnAction(e -> excelLoader.loadExcel(stage));
        saveBtn.setOnAction(e -> excelSaver.saveExcel(stage));

        buttonBar.getChildren().addAll(addProductBtn, loadBtn, saveBtn);

        Label footer = new Label("Copyright © 2025 JunSeok. All Rights Reserved.");
        footer.setPadding(new Insets(20, 0, 0, 0));

        root.getChildren().addAll(scrollPane, buttonBar, footer);

        productAdder.addProductRow();

        Scene scene = new Scene(root, 700, 600);
        stage.setTitle("Excel 가격표 생성기");
        stage.setScene(scene);
        stage.show();
    }

    private ScrollPane scrollPane() {
        ScrollPane scrollPane = new ScrollPane(controller.getProductListContainer());
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle(
            "-fx-background-color:transparent; -fx-background-insets: 0; -fx-padding: 0;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setBorder(Border.EMPTY);
        return scrollPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
