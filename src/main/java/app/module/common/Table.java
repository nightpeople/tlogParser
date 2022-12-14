package app.module.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 表信息
 */
public class Table {

    public String name;

    public String desc;

    public ArrayList<String> intFields;

    public LinkedHashMap<String, Field> fields;

    public static final HashMap<String, String> needBiggerMemoryCacheTables = new HashMap<>();

    public static final StringBuilder outputBiggerMemoryCacheNode = new StringBuilder();

    static {
        needBiggerMemoryCacheTables.put("PropFlow", "PropFlow");

        outputBiggerMemoryCacheNode.append("  <buffer tag>\n");
        outputBiggerMemoryCacheNode.append("    @type memory\n");
        outputBiggerMemoryCacheNode.append("    total_limit_size 1GB\n");
        outputBiggerMemoryCacheNode.append("  </buffer>\n");
    }

    public Table(String name, String desc) {
        this.name = name;
        this.desc = desc;
        intFields = new ArrayList<>();
        fields = new LinkedHashMap<>();
    }

    public Field addFields(String name, Field field){
        return fields.put(name, field);
    }

    public boolean addIntFields(String field){
        return intFields.add(field);
    }

    public void inputBuild(StringBuilder builder){
        builder.append("<filter tlog.").append(name).append(">\n");
        builder.append("  @type parser\n");
        builder.append("  key_name content\n");
        builder.append("  <parse>\n");
        builder.append("    @type csv\n");
        builder.append("    keys ").append(String.join(",", fields.keySet())).append("\n");
        builder.append("    delimiter |\n");
        builder.append("    parser_type fast\n");
        builder.append("    types ");
        for (String fld : intFields) {
            builder.append(fld).append(":integer,");
        }
        //删最后一个逗号
        builder.deleteCharAt(builder.length() - 1);
        builder.append("\n");
        builder.append("  </parse>\n");
        builder.append("</filter>\n");
        //留空行
        builder.append("\n");
    }

    public void outputBuild(StringBuilder builder){
        builder.append("<match tlog.").append(name).append(">\n");
        builder.append("  @type mysql_bulk\n");
        builder.append("  @include config.d/outputDB.conf\n");
        builder.append("  column_names ").append(String.join(",", fields.keySet())).append("\n");
        builder.append("  table ").append(name).append("\n");
        if (needBiggerMemoryCacheTables.containsKey(name)){
            builder.append(outputBiggerMemoryCacheNode);
        }
        builder.append("</match>\n");

        builder.append("\n");
    }

    @Override
    public String toString() {
        return name + ": " + desc + "\n";
    }
}
