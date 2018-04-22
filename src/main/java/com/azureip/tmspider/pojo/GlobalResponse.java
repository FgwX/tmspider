package com.azureip.tmspider.pojo;

import java.io.Serializable;
import java.util.List;

public class GlobalResponse<T> implements Serializable {

    public GlobalResponse() {
    }

    public GlobalResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    private String status;
    private String message;
    private T result;
    private List<T> resultList;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }
}
