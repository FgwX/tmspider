package com.azureip.tmspider.pojo;

import java.io.Serializable;
import java.util.Date;

public class AnnQueryPojo implements Serializable {
    public AnnQueryPojo() {
    }

    public AnnQueryPojo(int page, int rows, int annNum, String annType, Date appDateBegin, Date appDateEnd) {
        this.page = page;
        this.rows = rows;
        this.annNum = annNum;
        this.annType = annType;
        this.appDateBegin = appDateBegin;
        this.appDateEnd = appDateEnd;
    }

    private int page;
    private int rows;
    private int annNum;
    private String annType;
    private boolean totalYOrN = true;
    private Date appDateBegin;
    private Date appDateEnd;

    public int getPage() {
        return page;
    }

    public int getRows() {
        return rows;
    }

    public int getAnnNum() {
        return annNum;
    }

    public String getAnnType() {
        return annType;
    }

    public boolean isTotalYOrN() {
        return totalYOrN;
    }

    public Date getAppDateBegin() {
        return appDateBegin;
    }

    public Date getAppDateEnd() {
        return appDateEnd;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setAnnNum(int annNum) {
        this.annNum = annNum;
    }

    public void setAnnType(String annType) {
        this.annType = annType;
    }

    public void setTotalYOrN(boolean totalYOrN) {
        this.totalYOrN = totalYOrN;
    }

    public void setAppDateBegin(Date appDateBegin) {
        this.appDateBegin = appDateBegin;
    }

    public void setAppDateEnd(Date appDateEnd) {
        this.appDateEnd = appDateEnd;
    }
}
