package a.b.c.demo.service;

import a.b.c.demo.model.dto.BookInsertDto;
import a.b.c.demo.model.dto.BookListDto;
import a.b.c.demo.mapper.BookMapper;
import a.b.c.demo.model.Book;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookService {

  @Autowired
  BookMapper bookMapper;

  public PageInfo<Book> list(BookListDto queryDto) {
    PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
    Book book=new Book();
    book.setIsbn("");
    List<Book> list=bookMapper.selectList(book);
    return new PageInfo<>(list);
  }

  public Book insert(BookInsertDto bookInsertDto) {
    Book book=new Book();
    book.setBookName(bookInsertDto.getBookName());
    int up=bookMapper.insert(book);
    log.info("新增条数："+up);
    return book;
  }
}
