package io.junseok.pricemaker.excel;

import static io.junseok.pricemaker.util.Alert.showAlert;

import io.junseok.pricemaker.MainController;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSaver {
    private final MainController controller;

    public ExcelSaver(MainController controller) {
        this.controller = controller;
    }

    public void saveExcel(Stage stage) {
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
            for (javafx.scene.Node productNode : controller.getProductListContainer().getChildren()) {
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
                            String costStr = costField.getText().replace(",", "").trim();
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
            showAlert("오류", "엑셀 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
