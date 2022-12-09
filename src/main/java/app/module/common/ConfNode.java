package app.module.common;

/**
 * xml表节点
 */
public abstract class ConfNode {
    public final StringBuilder data;
    private Table table;

    public ConfNode(Table table) {
        this.table = table;
        data = new StringBuilder(128);
    }
}
