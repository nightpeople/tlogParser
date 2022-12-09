package app;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws DocumentException {
        // TODO args参数, tlog路径等
        URL resource = Main.class.getResource("/tlog.xml");
        assert resource != null;
        String path = resource.getPath();
        File file = new File(path);
        SAXReader reader = new SAXReader();
        Document document = reader.read(resource);

//        Element root = document.getRootElement();
//        for (Iterator<Element> it = root.elementIterator(); it.hasNext() ;) {
//            Element element = it.next();
//            String tableName = element.attributeValue("name");
//            for (Element field : element.elements()) {
//                String fieldName = field.attributeValue("name");
//                System.out.println(tableName + ": " + fieldName);
//            }
//            System.out.println("--------------------\n");
//        }
        Node node = document.selectSingleNode("/Log/RoleLogin");
        String name = node.valueOf("@name");
        System.out.println(name);
    }
}
