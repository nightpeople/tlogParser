package app.module.common;

/**
 * xml节点
 */
public abstract class ConfNode {
    public final StringBuilder data;


    public ConfNode() {
        data = new StringBuilder(128);
    }
}
