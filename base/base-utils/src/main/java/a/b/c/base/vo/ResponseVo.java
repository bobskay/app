package a.b.c.base.vo;

import lombok.Data;

@Data
public class ResponseVo<T> {
  public static final int  DEFAULT_ERROR_CODE=500;
  public static final int  DEFAULT_SUCCESS_CODE=200;
  public static final int  DEFAULT_VALIDATE_CODE=501;

  private int code;
  private String msg;
  private T data;
  private String detail;

  public static ResponseVo error(String message) {
    ResponseVo vo=new ResponseVo();
    vo.code=DEFAULT_ERROR_CODE;
    vo.msg=message;
    return vo;
  }

  public static ResponseVo success(Object data) {
    ResponseVo vo=new ResponseVo();
    vo.code=DEFAULT_SUCCESS_CODE;
    vo.data=data;
    return vo;
  }

  public static ResponseVo validate(String msg) {
    ResponseVo vo=new ResponseVo();
    vo.code=DEFAULT_VALIDATE_CODE;
    vo.msg=msg;
    return vo;
  }

}
