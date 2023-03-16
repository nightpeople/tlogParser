package app.module;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

import app.module.common.Field;
import app.module.common.Table;

/**
 * tlog.xml 日志解析器
 */
public class XMLParser {
    private static final Logger logger = LoggerFactory.getLogger(XMLParser.class);

    private final Document document;

    public final boolean lowerCase;

    private final StringBuilder inputParserConf;

    private final StringBuilder outputConf;

    public static final StringBuilder outputSource = new StringBuilder();

    public static final String INPUT_CONF_PATH = "inputParser.conf";
    public static final String OUTPUT_CONF_PATH = "output.conf";

    /**
     * 解析的tlog table
     * 表名 - Table结构
     */
    public LinkedHashMap<String, Table> tables = new LinkedHashMap<>();

    static {
        outputSource.append("\n");
        outputSource.append("<source>\n");
        outputSource.append("  @type forward\n");
        outputSource.append("  bind 0.0.0.0\n");
        outputSource.append("  port 7878\n");
        outputSource.append("</source>\n");
        outputSource.append("\n");
    }

    public XMLParser(Document document, boolean lowerCase) {
        this.lowerCase = lowerCase;
        this.document = document;
        inputParserConf = new StringBuilder(1024 * 16);
        inputParserConf.append("#filter csv parser plugin parse tlog to k:v json by specified tag\n");
        outputConf = new StringBuilder(1024 * 16);
        outputConf.append("##tlog data output to mysql\n");
    }

    public void parse() {
        Element rootElement = document.getRootElement();
        for (Element tableElement : rootElement.elements()) {
            //表结构
            String tableName = tableElement.attributeValue("name");
            String tableDesc = tableElement.attributeValue("desc", "");
            Table table = new Table(tableName, tableDesc, lowerCase, tableName);
            for (Element fieldElement : tableElement.elements()) {
                //TODO 这里要判断属性值是否为空,name,type,必须非空
                String fieldName = fieldElement.attributeValue("name");
                String rawType = fieldElement.attributeValue("type"); // 原始类型要经过转换
                String fieldDesc = fieldElement.attributeValue("desc");
                Field field = new Field(fieldName, rawType, fieldDesc, tableName);
                table.addFields(field.name, field);
                if (Field.convert2Integer.containsKey(field.type)) {
                    table.addIntFields(field.name);
                }
            }
            table.inputBuild(inputParserConf);
            table.outputBuild(outputConf);

            tables.put(table.name, table);
        }
        outputConf.append(outputSource);
    }

    public void output2File() {
        write2File(INPUT_CONF_PATH, inputParserConf);
        write2File(OUTPUT_CONF_PATH, outputConf);
    }

    public void write2File(String path, StringBuilder builder) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(""); //清空原文件
            fileWriter.write(builder.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
