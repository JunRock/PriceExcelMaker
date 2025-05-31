package io.junseok.pricemaker;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Main extends Application {

    private VBox productListContainer;
    private final DecimalFormat priceFormat = new DecimalFormat("#,###.##");

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        productListContainer = new VBox(10);

        ScrollPane scrollPane = new ScrollPane(productListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle(
            "-fx-background-color:transparent; -fx-background-insets: 0; -fx-padding: 0;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setBorder(Border.EMPTY);

        HBox buttonBar = new HBox(10);
        Button addProductBtn = new Button("+ 제품 추가");
        Button loadBtn = new Button("엑셀 불러오기(.xlsx)");
        Button saveBtn = new Button("엑셀 저장");

        addProductBtn.setOnAction(e -> addProductRow());
        loadBtn.setOnAction(e -> loadExcel(stage));
        saveBtn.setOnAction(e -> saveExcel(stage));

        buttonBar.getChildren().addAll(addProductBtn, loadBtn, saveBtn);

        Label footer = new Label("Copyright © 2025 JunSeok. All Rights Reserved.");
        footer.setPadding(new Insets(20, 0, 0, 0));

        root.getChildren().addAll(scrollPane, buttonBar, footer);

        addProductRow();

        Scene scene = new Scene(root, 700, 600);
        stage.setTitle("Excel 가격표 생성기");
        stage.setScene(scene);
        stage.show();
    }

    private void addProductRow() {
        VBox productBox = new VBox(5);
        TextField productNameField = createTextField("제품명", 300);

        productNameField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (productNameField.getText().length() > 100) {
                event.consume();
            }
        });

        VBox partListBox = new VBox(5);
        Button addPartBtn = new Button("+ 부품 추가");
        addPartBtn.setOnAction(e -> addPartRow(partListBox));

        addPartRow(partListBox);

        productBox.getChildren().addAll(
            new Label("제품명"),
            productNameField,
            new Label("부품 목록"),
            partListBox,
            addPartBtn,
            new Separator()
        );

        productListContainer.getChildren().add(productBox);
    }

    private void addPartRow(VBox container) {
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

        Button removeBtn = new Button("삭제");
        HBox row = new HBox(10, partNameField, costField, removeBtn);
        row.setPadding(new Insets(5));
        removeBtn.setOnAction(e -> container.getChildren().remove(row));

        container.getChildren().add(row);
        partNameField.setOnAction(e -> costField.requestFocus());
    }

    private TextField createTextField(String prompt, double width) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        return field;
    }

    private void loadExcel(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("엑셀 파일 선택");
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            productListContainer.getChildren().clear();

            String lastProduct = "";
            VBox currentProductBox = null;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String product = row.getCell(0).getStringCellValue();
                String part = row.getCell(1).getStringCellValue();
                double cost = row.getCell(2).getNumericCellValue();

                if (!product.equals(lastProduct)) {
                    currentProductBox = new VBox(5);
                    TextField productNameField = createTextField("제품명", 300);
                    productNameField.setText(product);

                    VBox partListBox = new VBox(5);
                    Button addPartBtn = new Button("+ 부품 추가");
                    addPartBtn.setOnAction(e -> addPartRow(partListBox));

                    currentProductBox.getChildren().addAll(
                        new Label("제품명"),
                        productNameField,
                        new Label("부품 목록"),
                        partListBox,
                        addPartBtn,
                        new Separator()
                    );

                    productListContainer.getChildren().add(currentProductBox);
                    lastProduct = product;
                }

                if (currentProductBox != null) {
                    VBox partListBox = (VBox) currentProductBox.getChildren().get(3);
                    TextField partNameField = createTextField("부품 이름", 200);
                    TextField costField = createTextField("원가", 100);
                    partNameField.setText(part);
                    costField.setText(String.valueOf(cost));

                    Button removeBtn = new Button("삭제");
                    HBox partRow = new HBox(10, partNameField, costField, removeBtn);
                    partRow.setPadding(new Insets(5));
                    removeBtn.setOnAction(e -> partListBox.getChildren().remove(partRow));
                    partListBox.getChildren().add(partRow);
                }
            }
        } catch (IOException e) {
            showAlert("오류", "엑셀 불러오기 실패: " + e.getMessage());
        }
    }

    private void saveExcel(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("엑셀 저장 위치 선택");
        fileChooser.setInitialFileName("가격표.xlsx");
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("가격표");
            String[] headers = {"제품", "부품", "원가", "-20%", "-30%", "-40%", "-50%"};

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontName("맑은 고딕");
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(false);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (javafx.scene.Node productNode : productListContainer.getChildren()) {
                if (productNode instanceof VBox productBox) {
                    TextField productNameField = (TextField) productBox.getChildren().get(1);
                    String productName = productNameField.getText().trim();
                    VBox partListBox = (VBox) productBox.getChildren().get(3);

                    List<Integer> mergeRows = new ArrayList<>();

                    for (javafx.scene.Node partNode : partListBox.getChildren()) {
                        if (partNode instanceof HBox partRow) {
                            TextField partNameField = (TextField) partRow.getChildren().get(0);
                            TextField costField = (TextField) partRow.getChildren().get(1);

                            String partName = partNameField.getText().trim();
                            String costStr = costField.getText().trim();
                            if (productName.isEmpty() || partName.isEmpty() || costStr.isEmpty()) {
                                continue;
                            }

                            double cost = Double.parseDouble(costStr);
                            Row row = sheet.createRow(rowIdx);
                            Object[] values = {productName, partName, cost,
                                cost * 0.8, cost * 0.7, cost * 0.6, cost * 0.5};
                            for (int i = 0; i < values.length; i++) {
                                Cell cell = row.createCell(i);
                                if (values[i] instanceof String) {
                                    cell.setCellValue((String) values[i]);
                                } else if (values[i] instanceof Double) {
                                    cell.setCellValue((Double) values[i]);
                                }
                                cell.setCellStyle(cellStyle);
                            }
                            mergeRows.add(rowIdx);
                            rowIdx++;
                        }
                    }

                    if (mergeRows.size() > 1) {
                        int start = mergeRows.get(0);
                        int end = mergeRows.get(mergeRows.size() - 1);
                        sheet.addMergedRegion(new CellRangeAddress(start, end, 0, 0));
                        Row mergedRow = sheet.getRow(start);
                        if (mergedRow != null) {
                            Cell mergedCell = mergedRow.getCell(0);
                            if (mergedCell != null) {
                                mergedCell.setCellStyle(cellStyle);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(new FileOutputStream(file));
            showAlert("저장 완료", "엑셀이 저장되었습니다:\n" + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("오류", "엑셀 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
