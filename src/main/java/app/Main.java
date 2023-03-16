package app;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import javax.sql.DataSource;

import app.module.DBLoader;
import app.module.FixedSqlParser;
import app.module.PartitionTool;
import app.module.TableComparator;
import app.module.XMLParser;
import app.module.common.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        // 取jar包同级tlog.xml文件
        String jarPath = new File("").getAbsolutePath();
        String tlogPath;
        String configPath; //server.config文件路径
        String partitionConfPath; //partition.config文件路径
        String fixedSqlPath; //fixed.sql文件路径
        URL resource;
        Document document;
        SAXReader saxReader = new SAXReader();
        if (File.separatorChar == jarPath.charAt(jarPath.length() - 1)) {
            tlogPath = jarPath + "tlog.xml";
            configPath = jarPath + "server.config";
            partitionConfPath = jarPath + "partition.config";
            fixedSqlPath = jarPath + "fixed.sql";
        } else {
            tlogPath = jarPath + File.separatorChar + "tlog.xml";
            configPath = jarPath + File.separatorChar + "server.config";
            partitionConfPath = jarPath + File.separatorChar + "partition.config";
            fixedSqlPath = jarPath + File.separatorChar + "fixed.sql";
        }

        File tlogFile = new File(tlogPath);
        File configFile = new File(configPath);
        File partitionFile = new File(partitionConfPath);
        File fixedSqlFile = new File(fixedSqlPath);
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

        // 找不到就取内部的partition.config
        Properties partitionProp = new Properties();
        if (!partitionFile.exists()) {
            partitionConfPath = "/partition.config";
            partitionProp.load(Main.class.getResourceAsStream(partitionConfPath));
        } else {
            partitionProp.load(new FileReader(partitionFile));
        }

        // 找不到就取内部的fixed.sql
        Scanner scanner;
        if (!fixedSqlFile.exists()) {
            scanner = new Scanner(Objects.requireNonNull(Main.class.getResourceAsStream("/fixed.sql")), StandardCharsets.UTF_8.name());
        } else {
            scanner = new Scanner(fixedSqlFile);
        }

        //slg_data库配置
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);

        boolean lowerCase = isLowerCaseTableNames(dataSource);

        FixedSqlParser fixedSqlParser = new FixedSqlParser(scanner);

        //加载mysql数据库表
        DBLoader dbLoader = new DBLoader(dataSource, lowerCase);
        dbLoader.load();
        //System.out.println(dbLoader);

        //解析tlog
        XMLParser xmlParser = new XMLParser(document, lowerCase);
        xmlParser.parse();
        //生成fluentd配置
        xmlParser.output2File();

        //tlog新增内容对比,并添加
        TableComparator.compareAndAdd(dbLoader, xmlParser, dataSource, fixedSqlParser);

        //新增分区
        PartitionTool partitionTool = new PartitionTool(partitionProp, dataSource, lowerCase);
        partitionTool.updatePartition();
    }

    /**
     * mysql lower_case_table_names 参数:
     * 0-区分大小写
     * 1-不区分
     * 2-不区分
     */
    public static boolean isLowerCaseTableNames(DataSource dataSource) throws SQLException {
        String value = Utils.fetchOne(dataSource, "show variables like 'lower_case_table_names'", "Value");
        return !Objects.equals("0", value);
    }
}
