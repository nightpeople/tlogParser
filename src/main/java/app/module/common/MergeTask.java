package app.module.common;

import java.util.ArrayList;

import javax.sql.DataSource;

public class MergeTask {

    private final DataSource dataSource;

    public ArrayList<MergeUnit> mergeUnits;

    private final int id;
    private final String mergeRange;

    private final int topActiveDay;
    private final int middleActiveDay;


    public MergeTask(DataSource dataSource, int id, String mergeRange, int topActiveDay, int middleActiveDay) {
        this.dataSource = dataSource;
        mergeUnits = new ArrayList<>();
        this.id = id;
        this.mergeRange = mergeRange;
        this.topActiveDay = topActiveDay;
        this.middleActiveDay = middleActiveDay;
    }

    public void loadUnits() {

    }
}
