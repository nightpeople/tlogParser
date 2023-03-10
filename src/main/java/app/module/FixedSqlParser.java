package app.module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 写死的sql
 * 逻辑:
 * 1.fixed.sql可以写建表语句和DML(insert等等)语句
 * 2.载入全部建表语句和对应的表名,载入所有DML语句
 * 3.TableComparator对比时,如果没有fixed.sql某个表,则执行建表语句,然后遍历DML语句,执行该表的所有DML语句
 */
public class FixedSqlParser {

    public static final Pattern pattern = Pattern.compile("CREATE TABLE(?: IF NOT EXISTS|) (\\w+)", Pattern.CASE_INSENSITIVE); //忽略大小写

    /**
     * tableName - sql
     */
    public final LinkedHashMap<String, String> tableSql;

    /**
     * 除建表外语句
     */
    public final ArrayList<String> dml;

    public FixedSqlParser(Scanner scanner) {
        LinkedHashMap<String, String> tableSql = new LinkedHashMap<>();
        ArrayList<String> dml = new ArrayList<>();
        //因为sql语句是用;分隔
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String sql = scanner.next().trim();
            Matcher matcher = pattern.matcher(sql);
            if (matcher.find()) {
                tableSql.put(matcher.group(1), sql);
            } else {
                dml.add(sql);
            }
        }
        this.tableSql = tableSql;
        this.dml = dml;

        scanner.close();
    }
}
