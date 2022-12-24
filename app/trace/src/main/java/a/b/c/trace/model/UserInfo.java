package a.b.c.trace.model;

import a.b.c.base.enums.GenderEnum;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private Long id;
    private String name;
    private String pwd;
    private Date createdAt;
}