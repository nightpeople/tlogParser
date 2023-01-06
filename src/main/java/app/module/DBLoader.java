package app.module;

import java.util.Properties;

/**
 * 加载mysql库所有表信息
 */
public class DBLoader {

    public Properties properties;

    public DBLoader(Properties properties) {
        this.properties = properties;
    }
}
