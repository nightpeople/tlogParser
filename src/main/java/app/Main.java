package app;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

import javax.sql.DataSource;

import app.module.DBLoader;
import app.module.PartitionTool;
import app.module.XMLParser;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO args参数, tlog路径等
        // 取jar包同级tlog.xml文件
        String jarPath = new File("").getAbsolutePath();
        String tlogPath;
        String configPath; //server.config文件
        String partitionConfPath; //partition.config文件
        URL resource;
        Document document;
        SAXReader saxReader = new SAXReader();
        if (File.separatorChar == jarPath.charAt(jarPath.length() - 1)) {
            tlogPath = jarPath + "tlog.xml";
            configPath = jarPath + "server.config";
            partitionConfPath = jarPath + "partition.config";
        } else {
            tlogPath = jarPath + File.separatorChar + "tlog.xml";
            configPath = jarPath + File.separatorChar + "server.config";
            partitionConfPath = jarPath + File.separatorChar + "partition.config";
        }

        File tlogFile = new File(tlogPath);
        File configFile = new File(configPath);
        File partitionFile = new File(partitionConfPath);
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

        //        System.out.println(properties.getProperty("db_url"));
        //        System.out.println(properties.getProperty("db_user"));
        //        System.out.println(properties.getProperty("db_passwd"));
        //        System.exit(0);

        //slg_data库
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);

        PartitionTool partitionTool = new PartitionTool(partitionProp, dataSource);
        partitionTool.updatePartition();

        DBLoader dbLoader = new DBLoader(dataSource);
        dbLoader.load();
        //        System.out.println(dbLoader);
        XMLParser xmlParser = new XMLParser(document);
        xmlParser.parse();

        xmlParser.output2File();
    }
}
