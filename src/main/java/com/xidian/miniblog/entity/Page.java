package com.xidian.miniblog.entity;

/**
 * @author qhhu
 * @date 2020/3/12 - 10:17
 */
public class Page {

    private int current = 1;
    private int offset = 0;
    private int limit = 10;
    private int rows;
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    /**
     * 获取当前页面数据的起始行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获得总页数
     */
    public int getTotal() {
        int total = rows / limit;
        if (rows % limit != 0) {
            total++;
        }
        return total;
    }

    /**
     * 获取当前页面显示的起始页码数
     */
    public int getFrom() {
        int from = current - 2;
        return from > 0 ? from : 1;
    }

    /**
     * 获取当前页面显示的结束页码数
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
