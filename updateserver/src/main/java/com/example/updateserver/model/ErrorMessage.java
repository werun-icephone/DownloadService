package com.example.updateserver.model;

import com.example.updateserver.model.type.ErrorType;

public class ErrorMessage {
    private long completeSize;
    private ErrorType errorType;

    public long getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
