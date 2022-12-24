package a.b.c.exchange.response;

import lombok.Data;

@Data
public class ApiResponse {
    private String request;
    private String response;
    private Integer code;
}
