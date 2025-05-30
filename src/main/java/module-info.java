module io.junseok.pricemaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens io.junseok.pricemaker to javafx.fxml;
    exports io.junseok.pricemaker;
}