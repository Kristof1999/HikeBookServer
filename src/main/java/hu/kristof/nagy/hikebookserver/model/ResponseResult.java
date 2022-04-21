package hu.kristof.nagy.hikebookserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ResponseResult<T> {
    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private String failMessage;
    private T successResult;

    private ResponseResult() {}

    public static <P> ResponseResult<P> fail(String failMessage) {
        return new ResponseResult<>(failMessage);
    }

    public static <P> ResponseResult<P> success(P successResult) {
        return new ResponseResult<>(successResult);
    }

    private ResponseResult(String failMessage) {
        this.failMessage = failMessage;
        isSuccess = false;
    }

    private ResponseResult(T successResult) {
        this.successResult = successResult;
        isSuccess = true;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public T getSuccessResult() {
        return successResult;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "isSuccess=" + isSuccess +
                ", failMessage='" + failMessage + '\'' +
                ", successResult=" + successResult +
                '}';
    }
}
