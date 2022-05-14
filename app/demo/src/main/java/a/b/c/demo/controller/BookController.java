package a.b.c.demo.controller;

import a.b.c.base.vo.ResponseVo;
import a.b.c.demo.model.dto.BookInsertDto;
import a.b.c.demo.model.dto.BookListDto;
import a.b.c.demo.model.Book;
import a.b.c.demo.service.BookService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
@Slf4j
public class BookController {

  @Autowired
  BookService bookService;

  @RequestMapping("/list")
  public ResponseVo<PageInfo<Book>> list(@RequestBody @Validated BookListDto bookListDto){
    PageInfo<Book> page=bookService.list(bookListDto);
    return ResponseVo.success(page);
  }

  @RequestMapping("/insert")
  public ResponseVo<Book> insert(@RequestBody @Validated BookInsertDto bookInsertDto){
    Book book= bookService.insert(bookInsertDto);
    return ResponseVo.success(book);
  }
}
