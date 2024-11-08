package me.findthepeach.findyourpet.common.constant.template;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReturnCode {


    RC100(100, "success"),
    RC400(400, "service failed"),
    RC401(401, "unauthorized"),
    RC404(404, "not found"),
    RC403(403, "forbidden"),
    RC500(500, "system error, try again later");

    /**
     * custom status code
     **/
    private final int code;
    /**
     * custom description
     **/
    private final String message;
}

