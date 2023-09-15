package com.bbva.core.abstracts;

public abstract class IDataResult<T> {
    public T data;
    public Boolean success;
    public String message;
    public String status;
}
