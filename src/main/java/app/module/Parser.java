package app.module;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.net.URL;

public class Parser {

    public URL url;

    public Document document;

    private final StringBuilder inputParserConf;

    private final StringBuilder outputConf;

    public Parser(URL url) throws DocumentException {
        this.url = url;
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(url);
        inputParserConf = new StringBuilder(1024 * 16);
        outputConf = new StringBuilder(1024 * 16);
    }

    public void parseXML(){

    }

    public void write2File(){

    }
}
