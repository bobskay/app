package a.b.c.base.web;

import lombok.Data;

@Data
public class Response<T> {
    public static final String CODE_SUCCESS = "200";

    private T data;
    private String code;
    private String detail;
    private String traceId;

    public static <T> Response<T> success(T data) {
        Response response = new Response();
        response.data = data;
        response.code = CODE_SUCCESS;
        return response;
    }
}
