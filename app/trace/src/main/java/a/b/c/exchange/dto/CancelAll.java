package a.b.c.exchange.dto;

import lombok.Data;
import a.b.c.exchange.response.ApiResponse;

@Data
public class CancelAll extends ApiResponse {
    /**
     * code : 200
     * msg : The operation of cancel all open order is done.
     */
    private Integer code;
    private String msg;
}

