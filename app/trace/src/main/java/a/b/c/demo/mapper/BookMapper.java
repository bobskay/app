package a.b.c.demo.mapper;

import a.b.c.base.dao.BaseMapper;
import a.b.c.demo.model.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

}
