package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.trace.component.strategy.Strategy;
import a.b.c.trace.component.strategy.vo.TaskInfoDto;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.dto.IdDto;
import a.b.c.trace.model.dto.OrderFilledDto;
import a.b.c.trace.model.vo.TaskInfoVo;
import a.b.c.trace.service.TaskInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("taskInfo")
public class TaskInfoController {

    @Resource
    TaskInfoService taskInfoService;

    @RequestMapping("list")
    public Response<List<TaskInfoVo>> list() {
        List<TaskInfoVo> list = taskInfoService.getAll();
        return Response.success(list);
    }

    @RequestMapping("insert")
    public Response<Integer> insert(@RequestBody TaskInfo taskInfo) {
        int update = taskInfoService.insert(taskInfo);
        return Response.success(update);
    }

    @RequestMapping("init")
    public Response<List<TaskInfoVo>> init() {
        taskInfoService.addTunBiBao();
        taskInfoService.addWangGe();
        return list();
    }

    @RequestMapping("run")
    public Response<TaskInfo> run(@RequestBody @Valid IdDto dto) {
        TaskInfo taskInfo = taskInfoService.getById(dto.getId());
        taskInfoService.run(taskInfo);
        return Response.success(taskInfo);
    }


    @RequestMapping("update")
    public Response<Integer> update(@RequestBody TaskInfoDto taskInfoDto) {
        taskInfoService.update(taskInfoDto);
        return Response.success(1);
    }

    @RequestMapping("filled")
    public Response<Integer> filled(@RequestBody OrderFilledDto orderFilledDto) {
        taskInfoService.filled(orderFilledDto);
        return Response.success(1);
    }
}
