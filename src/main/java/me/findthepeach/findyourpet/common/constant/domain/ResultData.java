package me.findthepeach.findyourpet.common.constant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.findyourpet.common.constant.template.ReturnCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ResultData<T> {

    private int status;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public ResultData(ReturnCode returnCode) {
        this.status = returnCode.getCode();
        this.message = returnCode.getMessage();
    }

    /**
     * Called when the request is successful
     * @param data The data part of the response
     * @return A successful request result
     * @param <T>
     */
    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        resultData.setData(data);

        return resultData;
    }

    /**
     * Called when the request is successful (without parameters)
     * @return A successful request result
     */
    public static ResultData<Object> success() {
        ResultData<Object> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());

        return resultData;
    }

    /**
     * Called when the request fails
     * @param code The error code
     * @param message The error message
     * @return A failure request result
     * @param <T>
     */
    public static <T> ResultData<T> fail(int code, String message) {
        return ResultData.<T>builder()
                .status(code)
                .message(message)
                .build();
    }
}