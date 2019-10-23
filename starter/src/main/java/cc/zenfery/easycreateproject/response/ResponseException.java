package cc.zenfery.easycreateproject.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseException extends RuntimeException {

    private String errorCode;

    // Message 占位参数，如 this is an {0} error
    private Object[] args;

    public ResponseException(String errorCode) {
        this.errorCode = errorCode;
    }

    public ResponseException(String errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }
}
