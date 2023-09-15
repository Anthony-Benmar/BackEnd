package com.bbva.core.results;

public class SuccessDataResult<T> extends DataResult<T> {
    public SuccessDataResult(T data) {
        super(data, true, "200", "Succesfull");
    }
    public SuccessDataResult(T data, String message) {
        super(data, true, "200",message);
    }
    public SuccessDataResult(T data,String status, String message) {
        super(data, true, status,message);
    }
}
