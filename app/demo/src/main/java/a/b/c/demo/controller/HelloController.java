package a.b.c.demo.controller;

import a.b.c.base.vo.ServiceException;
import a.b.c.base.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @RequestMapping("/")
  public ResponseVo<String> hello() {
    return ResponseVo.success("hello");
  }

  @RequestMapping("/doError")
  public ResponseVo<String> doError() {
    throw new RuntimeException("模拟异常");
  }

  @RequestMapping("/getMessage")
  public ResponseVo<String> getMessage() {
    throw new ServiceException("模拟自定义异常");
  }
}