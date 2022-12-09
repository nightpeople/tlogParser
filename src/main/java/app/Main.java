package app;

import app.module.Parser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) throws DocumentException {
        // TODO args参数, tlog路径等
        URL resource = Main.class.getResource("/tlog.xml");
        Parser parser = new Parser(resource);
        parser.parseXML();

        System.exit(1);
        assert resource != null;
        String path = resource.getPath();
        File file = new File(path);
        SAXReader reader = new SAXReader();
        Document document = reader.read(resource);

        Element root = document.getRootElement();
        for (Iterator<Element> it = root.elementIterator(); it.hasNext() ;) {
            Element element = it.next();
            String tableName = element.attributeValue("name");
            for (Element field : element.elements()) {
                String fieldName = field.attributeValue("name");
                System.out.println(tableName + ": " + fieldName);
            }
            System.out.println("--------------------\n");
        }

    }
}
