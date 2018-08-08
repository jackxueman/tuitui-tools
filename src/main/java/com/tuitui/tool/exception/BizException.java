package com.tuitui.tool.exception;


import com.tuitui.tool.enums.ApiResponseCode;

/**
 * @author liujianxue
 * @date 2017/12/28 14:58
 * @version v1.0.0
 * @mail 1071935039@qq.com
 */
public class BizException extends RuntimeException {

    private int code;

    private String msg;

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public static void parException(String msg) {
        throw new BizException(Integer.valueOf(ApiResponseCode.PARAM_ERR.get()), msg);
    }

    public static void parOutRangeException(String msg) {
        throw new BizException(Integer.valueOf(ApiResponseCode.PARAM_ERR.get()), msg);
    }

    public static void fail(int code, String msg) {
        throw new BizException(code, msg);
    }

    public static void fail(ApiResponseCode code, String msg) {
        String message = msg == null ? code.getName() : msg;
        throw new BizException(Integer.valueOf(code.get()), message);
    }

    public static void isNull(Object obj, String msg) {
        if (obj == null || obj.equals("")) {
            throw new BizException(Integer.valueOf(ApiResponseCode.PARAM_NIL.get()), msg);
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}