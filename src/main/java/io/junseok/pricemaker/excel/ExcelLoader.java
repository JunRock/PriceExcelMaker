package io.junseok.pricemaker.excel;

import static io.junseok.pricemaker.excel.productpart.ProductPartAdder.priceFormat;
import static io.junseok.pricemaker.util.Alert.showAlert;
import static io.junseok.pricemaker.util.ButtonComponent.createButton;
import static io.junseok.pricemaker.util.Text.createTextField;

import io.junseok.pricemaker.MainController;
import io.junseok.pricemaker.excel.productpart.ProductPartAdder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelLoader {

    private final MainController controller;
    private final ProductPartAdder productPartAdder;

    public ExcelLoader(MainController controller, ProductPartAdder productPartAdder) {
        this.controller = controller;
        this.productPartAdder = productPartAdder;
    }

    public void loadExcel(Stage stage) {
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
            controller.getProductListContainer().getChildren().clear();

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
                    Button addPartBtn = createButton("+ 부품 추가");
                    addPartBtn.setOnAction(e -> productPartAdder.addPartRow(partListBox));

                    currentProductBox.getChildren().addAll(
                        new Label("제품명"),
                        productNameField,
                        new Label("부품 목록"),
                        partListBox,
                        addPartBtn,
                        new Separator()
                    );

                    controller.getProductListContainer().getChildren().add(currentProductBox);
                    lastProduct = product;
                }

                if (currentProductBox != null) {
                    VBox partListBox = (VBox) currentProductBox.getChildren().get(3);
                    TextField partNameField = createTextField("부품 이름", 200);
                    TextField costField = createTextField("원가(₩)", 100);
                    partNameField.setText(part);
                    costField.setText(priceFormat.format(cost));

                    Button removeBtn = createButton("삭제");
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
}
