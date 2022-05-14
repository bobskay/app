package a.b.c.base.vo;

import a.b.c.base.annotation.Remark;
import lombok.Getter;

@Remark("自定义异常")
public class ServiceException extends RuntimeException{

  @Getter
  private String message;

  public ServiceException(String message){
    this.message=message;
  }
}
