package a.b.c.exchange.dto;

import lombok.Data;
import a.b.c.exchange.response.ApiResponse;

@Data
public class UserDataStream extends ApiResponse {
    private String listenKey;
}
