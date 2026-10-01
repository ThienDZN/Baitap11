package vn.iotstar.config;

import java.io.File;

public final class UploadConstants_24162120 {
    public static final String DIR = AppProperties_24162120.get("app.upload.dir",
            System.getProperty("user.home") + File.separator + "uploads" + File.separator + "assignment05-admincrud-springboot4");

    private UploadConstants_24162120() {
    }
}
