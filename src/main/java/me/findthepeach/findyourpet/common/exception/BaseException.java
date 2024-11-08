package me.findthepeach.findyourpet.common.exception;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.findthepeach.findyourpet.common.constant.template.ReturnCode;

@Data
@Builder
@NoArgsConstructor
public class BaseException extends RuntimeException {

    private ReturnCode returnCode;
    private String message;

    public BaseException(ReturnCode returnCode) {
        super(returnCode.getMessage());
        this.returnCode = returnCode;
    }

    public BaseException(ReturnCode returnCode, String message) {
        super(message);
        this.returnCode = returnCode;
        this.message = message;
    }
}