package net.nemisolv.techshop.core.exception;

import lombok.Getter;
import lombok.Setter;
import net.nemisolv.techshop.util.ResultCode;

@Getter
@Setter

public class BadRequestException extends RuntimeException {
    private ResultCode resultCode;
    public BadRequestException(ResultCode resultCode) {
        super(resultCode.message());
    }

    public BadRequestException(ResultCode resultCode,String customMessage) {
        super(customMessage);
    }
}
