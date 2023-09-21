package com.bbva.core.results;

public class ErrorDataResult<T> extends DataResult<T> {
    public ErrorDataResult(T data){
        super(data, false,"500", "Failed");
    }
    public ErrorDataResult(T data, String status,  String message) {
        super(data, false, status, message);
    }
}
