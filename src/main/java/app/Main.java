package app;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import app.module.DBLoader;
import app.module.XMLParser;

public class Main {
    public static void main(String[] args) throws DocumentException, MalformedURLException {
        // TODO args参数, tlog路径等
        // 取jar包同级tlog.xml文件
        String jarPath = new File("").getAbsolutePath();
        String tlogPath;
        URL resource;
        Document document;
        SAXReader saxReader = new SAXReader();
        if (File.separatorChar == jarPath.charAt(jarPath.length() - 1)) {
            tlogPath = jarPath + "tlog.xml";
        } else {
            tlogPath = jarPath + File.separatorChar + "tlog.xml";
        }

        File tlogFile = new File(tlogPath);
        // 找不到就取内部的tlog.xml
        if (!tlogFile.exists()) {
            tlogPath = "/tlog.xml";
            resource = Main.class.getResource(tlogPath);
            document = saxReader.read(resource);
        } else {
            document = saxReader.read(tlogFile);
        }

        XMLParser XMLParser = new XMLParser(document);
        DBLoader dbLoader = new DBLoader();
        XMLParser.parse();

    }
}
