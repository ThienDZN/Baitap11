package vn.iotstar.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ProjectStructureTest_24162120 {
    @Test
    void uploadDirectoryConstantShouldExist() {
        assertNotNull(UploadConstants_24162120.DIR);
    }

    @Test
    void productPageSizeShouldLoad() {
        assertNotNull(AppProperties_24162120.get("app.product.page-size", "6"));
    }
}
