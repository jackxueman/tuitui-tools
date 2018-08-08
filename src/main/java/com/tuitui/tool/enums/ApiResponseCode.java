package com.tuitui.tool.enums;

/**
 * @author liujianxue
 * @date 2018/01/05 10:53
 * @version v1.0.0
 * @mail 1071935039@qq.com
 */
public enum ApiResponseCode {
    SUCCESS(0, "成功"),

    // 兼容前端：认证和授权都返回401
    AUTH_FAIL(401, "认证失败"),
    ACCESS_DENIED(401, "授权失败"),
    SESSION_INVALID(405, "SESSION失效"),
    SESSION_TIMEOUT(401, "SESSION超时"),

    UN_KNONW_ERROR(10000, "未知异常"),
    METHOD_UN_IMPL(10001, "运行异常，当前操作未实现"),
    ILLEGAL_REQ(10004, "非法请求"),

    NETWORK_EXP(10020, "网络异常"),
    NETWORK_TIMEOUT(10021, "网络超时"),
    EXTERNAL_SERVICE__EXP(10022, "外部服务器异常"),
    EXTERNAL_SERVICE_TIMEOUT(10023, "外部服务器超时"),

    DB_ERROR(10040, "数据库操作异常"),
    DB_TIMEOUT(10041, "数据库访问超时"),
    DATA_EXIST(10042, "数据已存在"),
    DATA_NOT_EXIST(10043, "数据不存在"),

    PARAM_NIL(10060, "参数不能为空值"),
    PARAM_ERR(10061, "参数错误"),
    PARAM_OUT_RANGE(10062, "参数值超出允许范围"),
    PARAM_OUT_LEN(10063, "参数值长度超过限制"),

    NAME_PASSWORD_ERROR(20000, "用户名或密码错误"),
    USERNAME_NOT_EXIST(20001, "用户名不存在"),
    USER_INVALID(20002, "账号已冻结"),
    IP_LIMIT(20003, "IP限制"),
    PASSWORD_INVALID(20004, "密码错误"),

    DATASOURCE_NAME_EXIST(20020, "数据源名称已存在"),
    API_NAME_EXIST(20021, "接口名称已存在"),
    CUSTOMER_NAME_EXIST(20022, "客户（单位）名称已存在"),

    PDF_NOT_ENCRYPT(20040, "PDF未加密"),
    PDF_ENCRYPT(20041, "PDF不允许加密"),
    PDF_NOT(20042, "非PDF文件");

    private String name;
    private int code;

    private ApiResponseCode(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public static String getNameByCode(int code) {
        ApiResponseCode[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ApiResponseCode item = var1[var3];
            if (item.get() == code) {
                return item.getName();
            }
        }

        return null;
    }

    public static ApiResponseCode getByCode(int code) {
        ApiResponseCode[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ApiResponseCode item = var1[var3];
            if (item.get() == code) {
                return item;
            }
        }

        return null;
    }

    public int get() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
