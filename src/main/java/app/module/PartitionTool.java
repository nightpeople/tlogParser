package app.module;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import app.module.common.RangeType;

/**
 * 为数据库表增加分区
 * 只实现range分区方式
 */
public class PartitionTool {
    private static final Logger logger = LoggerFactory.getLogger(PartitionTool.class);

    private final Properties partitionProp;
    private final DataSource dataSource;
    public final boolean lowerCase;

    public PartitionTool(Properties partitionProp, DataSource dataSource, boolean lowerCase) {
        this.partitionProp = partitionProp;
        this.dataSource = dataSource;
        this.lowerCase = lowerCase;
    }

    public void updatePartition() throws SQLException, ParseException {
        logger.info("开始分区检查");
        //遍历每行表配置
        for (Entry<Object, Object> row : partitionProp.entrySet()) {
            String tableName = lowerCase ? ((String) row.getKey()).toLowerCase() : (String) row.getKey();
            String rawType = (String) row.getValue();
            if (Strings.isNullOrEmpty(rawType)) {
                //默认rangeType为month
                rawType = "month";
            }
            RangeType type = RangeType.valueOf(rawType);
            StringBuilder createBuilder = new StringBuilder();
            if (isPartitionTable(tableName, createBuilder)) {
                checkAndAddPartition(tableName, type, createBuilder);
            } else {
                alter2PartitionTable(tableName, type);
            }
        }
    }

    /**
     * 获取建表语句,判断现在是分区表还是普通表
     */
    private boolean isPartitionTable(String tableName, StringBuilder builder) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("show create table " + tableName)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String createSql = rs.getString(2);
                        if (createSql.contains("PARTITION") || createSql.contains("partition")) {
                            builder.append(createSql);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 普通表转分区表
     */
    private void alter2PartitionTable(String tableName, RangeType type) throws ParseException, SQLException {
        logger.info("{} 表转换为分区表, 分区间隔: {}", tableName, type.name());
        StringBuilder builder = new StringBuilder("alter table " + tableName + " partition by range (to_days(dt))\n(\n");
        type.buildPartition(builder, null);
        //最后加上max_border分区,形成闭合的分区构成
        builder.append("partition max_border values less than MAXVALUE\n)");
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(builder.toString())) {
                int i = ps.executeUpdate();
            }
        }
    }

    /**
     * 根据RangeType类型添加分区
     */
    private void checkAndAddPartition(String tableName, RangeType type, StringBuilder createBuilder) throws SQLException, ParseException {
        StringBuilder lastPartitionBuilder = new StringBuilder();
        //不符合添加分区条件,跳过本次任务
        if (!checkAddPartition(tableName, type, createBuilder, lastPartitionBuilder)) {
            return;
        }
        logger.info("{}表添加分区, 分区间隔: {}", tableName, type.name());
        //先删掉max_border分区
        String deleteSql = "alter table " + tableName + " drop partition max_border";
        executeUpdate(deleteSql);
        //加分区sql
        StringBuilder partitionBuilder = new StringBuilder("alter table " + tableName + " add partition\n(\n");
        type.buildPartition(partitionBuilder, RangeType.FORMATTER_MINI.parse(lastPartitionBuilder.toString()));
        //最后加上max_border分区,形成闭合的分区构成
        partitionBuilder.append("partition max_border values less than MAXVALUE\n)");
        executeUpdate(partitionBuilder.toString());
    }

    private boolean checkAddPartition(String tableName, RangeType type, StringBuilder createBuilder, StringBuilder lastPartitionBuilder) {
        //策略: 正则匹配出,除max_border(最大右边界)的所有分区名称,最后一个分区名称为最后的分区日期,交由RangeType判断本次是否要新增分区
        Pattern pattern = Pattern.compile("PARTITION p(\\d+?) VALUES", Pattern.CASE_INSENSITIVE); //忽略大小写
        Matcher matcher = pattern.matcher(createBuilder);
        String lastPartition = "";
        while (matcher.find()) {
            lastPartition = matcher.group(1);
        }
        lastPartitionBuilder.append(lastPartition);
        LocalDate lastPartitionDate = LocalDate.parse(lastPartition, RangeType.PATTERN_MINI);
        return type.needAddPartition(lastPartitionDate);
    }

    private void executeUpdate(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int i = ps.executeUpdate();
            }
        }
    }
}
