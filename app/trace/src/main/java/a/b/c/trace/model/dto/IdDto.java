package a.b.c.trace.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IdDto {
    @NotNull(message = "主键不能为空")
    private Long id;
}
