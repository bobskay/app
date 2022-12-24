package a.b.c.trace.model.dto;

import a.b.c.base.dto.PageQueryDto;
import a.b.c.base.enums.GenderEnum;
import a.b.c.trace.model.UserInfo;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
public class UserListDto extends PageQueryDto<UserInfo> {
    private Date addTimeStart;
    private Date addTimeEnd;
    private List<String> nameList;
    private String name;
    private GenderEnum gender;
}
