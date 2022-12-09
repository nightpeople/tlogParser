package app.module.common;

import java.util.HashMap;

public class Field {

    public String table;

    public String name;

    /**
     * 当前类型只有
     * varchar, int, tinyint, bigint, datetime
     */
    public String type;

    public int size;

    public String desc;

    /**
     * tlog type属性 mysql字段类型对照表
     * tlogType : mysqlType
     */
    public static final HashMap<String, String> typeParser = new HashMap<>();

    /**
     * mysql type 对应的长度
     * type : size
     */
    public static final HashMap<String, Integer> typeSize = new HashMap<>();

    public static final HashMap<String, String> convert2Integer = new HashMap<>();

    static {
        typeParser.put("string", "varchar");
        typeParser.put("uint", "int");
        typeParser.put("int", "int");
        typeParser.put("utinyint", "tinyint");
        typeParser.put("tinyint", "tinyint");
        typeParser.put("bigint", "bigint");
        typeParser.put("datetime", "datetime");

        typeSize.put("varchar", 255);
        typeSize.put("int", 11);
        typeSize.put("tinyint", 3);
        typeSize.put("bigint", 20);
        typeSize.put("datetime", 0); //0不注明长度

        convert2Integer.put("int", "int");
        convert2Integer.put("tinyint", "tinyint");
        convert2Integer.put("bigint", "bigint");
    }

    public Field(String name, String rawType, String desc, String table) {
        this.name = name;
        this.desc = desc;
        this.table = table;
        parseType(rawType, name, table);
    }

    public void parseType(String rawType, String name, String table){
        String type = typeParser.get(rawType);
        if (type == null){
            // TODO 优化抛出异常方式
            throw new IllegalArgumentException("tlog " + table + "表 " + name + "字段 type有未识别的类型 " + rawType);
        }
        this.type = type;

        Integer size = typeSize.get(type);
        if (size == null){
            // TODO 优化抛出异常方式
            throw new IllegalArgumentException(type + "类型没有指定对应的size长度");
        }
        this.size = size;
    }
}
