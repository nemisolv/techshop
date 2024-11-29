package net.nemisolv.techshop.exception;

import net.nemisolv.techshop.util.ResultCode;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(ResultCode result) {
        super(result.message());
    }
}
