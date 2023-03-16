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
                builder.append("partition p").append(FORMATTER_MINI.format(compareDate)).append(" values less than (to_days('")
                        .append(FORMATTER_NORMAL.format(compareDate)).append("')),\n");
                Instant plusInstant = compareDate.toInstant().plus(1, ChronoUnit.DAYS);
                compareDate = Date.from(plusInstant);
            }
            //删最后一个逗号
            //            builder.deleteCharAt(builder.length() - 1);
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
    month(1) {
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
                builder.append("partition p").append(PATTERN_MINI.format(compareDate)).append(" values less than (to_days('")
                        .append(PATTERN_NORMAL.format(compareDate)).append("')),\n");
                compareDate = compareDate.with(TemporalAdjusters.firstDayOfNextMonth());
            }
            //删最后一个逗号
            //            builder.deleteCharAt(builder.length() - 1);
        }

        @Override
        public boolean needAddPartition(LocalDate lastPartitionDate) {
            //策略: 添加从当前月开始近3个月的分区(包含当前月)
            LocalDate curDate = LocalDate.now();
            //校正右区间
            LocalDate borderDate = curDate.plusMonths(MONTH_ADD).with(TemporalAdjusters.firstDayOfNextMonth());
            return borderDate.isAfter(lastPartitionDate);
        }
    }, threeMonth(2);// 匹配自然月

    //分区的步长,可以是月数,也可以是天数,根据不同的类型自己定义
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

    /**
     * 默认实现: 加length个month的分区
     */
    public void buildPartition(StringBuilder builder, Date lastPartitionDate) throws ParseException {
        //策略: 当天到最后一个分区(最后分区必须>当天日期)<30天,则创建下一个分区;当最后一个分区在当天左边,则必须先补齐分区一直到>当天日期(最后分区到当天右侧)
        LocalDate curDate = LocalDate.now();
        LocalDate lastPartDate; //最后分区
        LocalDate nextPartDate; //下个分区
        //当普通表转分区表时,lastPartitionDate为null
        boolean isPartitionTable = lastPartitionDate != null;
        if (lastPartitionDate == null) {
            //是普通表,用curDate算前后分区
            nextPartDate = curDate.plusMonths(length).with(TemporalAdjusters.firstDayOfNextMonth());
            //算出虚拟的上一个分区(最后分区)
            //    lastPartDate  -->  |curDate|  -->  nextPartDate
            lastPartDate = nextPartDate.plusMonths((length + 1) * -1);
        } else {
            lastPartDate = lastPartitionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        //当最后一个分区在当天左边,则必须先补齐分区一直到>当天日期(最后分区到当天右侧)
        while (!lastPartDate.isAfter(curDate)) {
            lastPartDate = lastPartDate.plusMonths(length).with(TemporalAdjusters.firstDayOfNextMonth());
            builder.append("partition p").append(PATTERN_MINI.format(lastPartDate)).append(" values less than (to_days('")
                    .append(PATTERN_NORMAL.format(lastPartDate)).append("')),\n");
        }

        //当天到最后一个分区间隔<30天,就创建下一个分区
        LocalDate targetDate = lastPartDate;
        while (targetDate.toEpochDay() - curDate.toEpochDay() < 30) {
            targetDate = targetDate.plusMonths(length).with(TemporalAdjusters.firstDayOfNextMonth());
            builder.append("partition p").append(PATTERN_MINI.format(targetDate)).append(" values less than (to_days('")
                    .append(PATTERN_NORMAL.format(targetDate)).append("')),\n");
        }
    }

    /**
     * 默认实现: 判断是否加length个month的分区
     */
    public boolean needAddPartition(LocalDate lastPartitionDate) {
        //策略: 当天到最后一个分区(最后分区必须>当天日期)<30天,则创建下一个分区;当最后一个分区在当天左边,则必须先补齐分区一直到>当天日期(最后分区到当天右侧)
        LocalDate curDate = LocalDate.now();
        if (lastPartitionDate == null) {
            //普通表转分区表
            return true;
        }
        //处理分区表,有lastPartitionDate
        //当最后一个分区在当天左边,则必须先补齐分区一直到>当天日期(最后分区到当天右侧)
        if (!lastPartitionDate.isAfter(curDate)) {
            return true;
        }
        //当天到最后一个分区间隔<30天,就创建下一个分区
        return lastPartitionDate.toEpochDay() - curDate.toEpochDay() < 30;
    }

    //TODO ---- 后续暂未实现

    /**
     * 专属加多天的分区的实现,例如threeDays,sevenDays等等,天数用步长length
     * 套在buildPartition()方法内使用
     */
    public void buildDaysPartition(StringBuilder builder, Date lastPartitionDate) throws ParseException {
    }

    /**
     * 专属多天分区用
     * 套在needAddPartition()方法内使用
     */
    public boolean needAddDaysPartition(LocalDate lastPartitionDate) {
        return false;
    }
}
