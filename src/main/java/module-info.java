module io.junseok.pricemaker {
    requires javafx.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    exports io.junseok.pricemaker;
    exports io.junseok.pricemaker.excel.product;
    exports io.junseok.pricemaker.excel.productpart;
    exports io.junseok.pricemaker.excel;
    exports io.junseok.pricemaker.util;
}
