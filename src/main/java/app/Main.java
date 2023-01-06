package app;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

import app.module.DBLoader;
import app.module.XMLParser;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO args参数, tlog路径等
        // 取jar包同级tlog.xml文件
        String jarPath = new File("").getAbsolutePath();
        String tlogPath;
        String configPath; //server.config文件
        URL resource;
        Document document;
        SAXReader saxReader = new SAXReader();
        if (File.separatorChar == jarPath.charAt(jarPath.length() - 1)) {
            tlogPath = jarPath + "tlog.xml";
            configPath = jarPath + "server.config";
        } else {
            tlogPath = jarPath + File.separatorChar + "tlog.xml";
            configPath = jarPath + File.separatorChar + "server.config";
        }

        File tlogFile = new File(tlogPath);
        File configFile = new File(configPath);
        // 找不到就取内部的tlog.xml
        if (!tlogFile.exists()) {
            tlogPath = "/tlog.xml";
            resource = Main.class.getResource(tlogPath);
            document = saxReader.read(resource);
        } else {
            document = saxReader.read(tlogFile);
        }

        // 找不到就取内部的server.config
        Properties properties = new Properties();
        if (!configFile.exists()) {
            configPath = "/server.config";
            properties.load(Main.class.getResourceAsStream(configPath));
        } else {
            properties.load(new FileReader(configFile));
        }

        //        System.out.println(properties.getProperty("db_url"));
        //        System.out.println(properties.getProperty("db_user"));
        //        System.out.println(properties.getProperty("db_passwd"));
        //        System.exit(0);

        DBLoader dbLoader = new DBLoader(properties);
        dbLoader.load();
        //        System.out.println(dbLoader);
        XMLParser xmlParser = new XMLParser(document);
        xmlParser.parse();


        xmlParser.output2File();
    }
}
