package a.b.c.web;

import lombok.Data;

@Data
public class ErrorVo {

  private Integer status;
  private String statusMessage;
  private String path;
  private String contentType;
  private String timestamp;
}
