package net.nemisolv.techshop.core.exception;

import lombok.Getter;
import lombok.Setter;
import net.nemisolv.techshop.util.ResultCode;

@Getter
@Setter

public class ResourceNotFoundException extends RuntimeException {
    private ResultCode resultCode;

    public ResourceNotFoundException(ResultCode resultCode) {
        super(resultCode.message());
        this.resultCode = resultCode;
    }
    public ResourceNotFoundException(ResultCode resultCode,String customMessage) {
        super(customMessage);
        this.resultCode = resultCode;
    }
}
