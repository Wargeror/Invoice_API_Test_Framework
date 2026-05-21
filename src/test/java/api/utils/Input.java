package api.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Input {
    private Properties properties;

    public Input() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
