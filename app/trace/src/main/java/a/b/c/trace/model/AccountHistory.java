package a.b.c.trace.model;

import a.b.c.base.annotation.Remark;
import lombok.Data;

import java.util.Date;

@Data
public class AccountHistory {

    private Long id;

    private Date createdAt;

    private String accountInfo;
}
