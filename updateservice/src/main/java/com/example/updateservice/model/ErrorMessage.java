package com.example.updateservice.model;

import com.example.updateservice.model.type.ErrorType;

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
