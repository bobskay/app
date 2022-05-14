package a.b.c.web;

import a.b.c.Constant;
import a.b.c.base.vo.ResponseVo;
import a.b.c.base.vo.ServiceException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@Slf4j
public class MyErrorController implements ErrorController {


  @ExceptionHandler()
  @ResponseBody
  public Object handleException(HttpServletRequest request, Exception e) {
    if (e instanceof ServiceException) {
      log.info(request.getRequestURI() + "-" + "发生自定义异常：" + e.getMessage());
      return ResponseVo.error(e.getMessage());
    }

    if (e instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
      List<ObjectError> list = validException.getBindingResult().getAllErrors();
      if (list.size() == 0) {
        return ResponseVo.validate("参数校验失败");
      }
      String message = list.get(0).getDefaultMessage();
      log.info(request.getRequestURI() + "-" + "参数校验失败：" + message);
      return ResponseVo.validate(message);
    }

    ResponseVo error= ResponseVo.error("服务端异常");
    log.error(request.getRequestURI() + "-" + "发生未知异常：" + e.getClass().getName(), e);
    if(Constant.DEBUG){
      error.setDetail(e.getMessage());
    }
    return error;
  }

  protected HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    } else {
      try {
        return HttpStatus.valueOf(statusCode);
      } catch (Exception var4) {
        return HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
  }

  /**
   * 实现错误路径,暂时无用
   */
  @Override
  public String getErrorPath() {
    return "";
  }

}