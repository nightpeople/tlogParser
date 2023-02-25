package app.module.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * mysql partition range分区方式
 */
public enum RangeType {
    day(1) {
        @Override
        public void buildPartition(StringBuilder builder, Date lastPartitionDate) throws ParseException {
            //策略: 添加从当前时间开始近7天的分区(包含当天)
            String curDay = FORMATTER_MINI.format(new Date());
            Date curDate = FORMATTER_MINI.parse(curDay);
            //当普通表转分区表时,lastPartitionDate为null
            boolean isPartitionTable = lastPartitionDate != null;
            if (lastPartitionDate == null) {
                lastPartitionDate = curDate;
            }
            Instant borderInstant = curDate.toInstant().plus(DAY_ADD, ChronoUnit.DAYS);
            Date borderDate = Date.from(borderInstant); //右区间
            //若两个时间相等,或者lastPartitionDate大于borderDate则跳过本次任务
            if (!borderDate.after(lastPartitionDate)) {
                return;
            }
            if (isPartitionTable) {
                //是添加分区,则左区间+1天
                lastPartitionDate = Date.from(lastPartitionDate.toInstant().plus(1, ChronoUnit.DAYS));
            }
            Date compareDate = lastPartitionDate;
            while (borderDate.compareTo(compareDate) >= 0) {
                builder.append("partition ").append(FORMATTER_MINI.format(compareDate)).append(" values less than (to_days('")
                        .append(FORMATTER_NORMAL.format(compareDate)).append("')),\n");
                Instant plusInstant = compareDate.toInstant().plus(1, ChronoUnit.DAYS);
                compareDate = Date.from(plusInstant);
            }
            //删最后一个逗号
            builder.deleteCharAt(builder.length() - 1);
        }

        @Override
        public boolean needAddPartition(LocalDate lastPartitionDate) {
            //策略: 添加从当前时间开始近7天的分区(包含当天)
            LocalDate curDate = LocalDate.now();
            LocalDate borderDate = curDate.plusDays(DAY_ADD);
            //若两个时间相等,或者lastPartitionDate大于borderDate则跳过本次任务
            return borderDate.isAfter(lastPartitionDate);
        }
    }, week(7), halfMonth(15), // 月末日期匹配自然月
    month(30) {
        // 匹配自然月
        @Override
        public void buildPartition(StringBuilder builder, Date lastPartitionDate) throws ParseException {
            //策略: 添加从当前月开始近3个月的分区(包含当前月)
            LocalDate curDate = LocalDate.now();
            LocalDate lastPartDate; //左区间
            //当普通表转分区表时,lastPartitionDate为null
            boolean isPartitionTable = lastPartitionDate != null;
            if (lastPartitionDate == null) {
                lastPartDate = curDate;
                //校正左分区日期
                lastPartDate = lastPartDate.with(TemporalAdjusters.firstDayOfNextMonth());
            } else {
                lastPartDate = lastPartitionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            //右区间
            LocalDate borderDate = curDate.plusMonths(MONTH_ADD).with(TemporalAdjusters.firstDayOfNextMonth());
            //若两个时间相等,或者lastPartDate大于borderDate则跳过本次任务
            if (!borderDate.isAfter(lastPartDate)) {
                return;
            }
            if (isPartitionTable) {
                //是添加分区,则左区间+1月
                lastPartDate = lastPartDate.with(TemporalAdjusters.firstDayOfNextMonth());
            }
            LocalDate compareDate = lastPartDate;
            while (!borderDate.isBefore(compareDate)) {
                builder.append("partition ").append(PATTERN_MINI.format(compareDate)).append(" values less than (to_days('")
                        .append(PATTERN_NORMAL.format(compareDate)).append("')),\n");
                compareDate = compareDate.with(TemporalAdjusters.firstDayOfNextMonth());
            }
            //删最后一个逗号
            builder.deleteCharAt(builder.length() - 1);
        }

        @Override
        public boolean needAddPartition(LocalDate lastPartitionDate) {
            //策略: 添加从当前月开始近3个月的分区(包含当前月)
            LocalDate curDate = LocalDate.now();
            //校正右区间
            LocalDate borderDate = curDate.plusMonths(MONTH_ADD).with(TemporalAdjusters.firstDayOfNextMonth());
            return borderDate.isAfter(lastPartitionDate);
        }
    }, threeMonth(90);// 匹配自然月

    //分区的天数(步长)
    public final int length;

    public static final SimpleDateFormat FORMATTER_MINI = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat FORMATTER_NORMAL = new SimpleDateFormat("yyyy-MM-dd");

    public static final DateTimeFormatter PATTERN_MINI = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter PATTERN_NORMAL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //---------------下面是配置的时间长度常量---------------
    public static final int DAY_ADD = 6; //添加从当前时间开始近7天的分区(包含当天)
    public static final int MONTH_ADD = 2; //添加从当前月开始近3个月的分区(包含当前月)

    RangeType(int length) {
        this.length = length;
    }

    public void buildPartition(StringBuilder builder, Date lastPartitionDate) throws ParseException {
    }

    public boolean needAddPartition(LocalDate lastPartitionDate) {
        return false;
    }
}
