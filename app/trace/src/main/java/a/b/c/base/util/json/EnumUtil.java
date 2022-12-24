package a.b.c.base.util.json;

import a.b.c.Constant;
import a.b.c.base.enums.DbEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EnumUtil {
    /**
     * 所有实现DbEnum接口的枚举类
     */
    @SneakyThrows
    public  static List<Class> allDbEnum() {
        List<Class> list = new ArrayList();
        String basePath = Constant.BASE_PACKAGE.replace('.', '/');
        String path = basePath + "/**/*Enum.class";
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resource = resourcePatternResolver.getResources("classpath:" + path);
        for (Resource r : resource) {
            String file = r.getFile().getAbsolutePath().replace('\\', '/');
            int idx = file.indexOf(basePath);
            String className = file.substring(idx).replace('/', '.').replaceAll(".class$", "");
            Class clazz = Class.forName(className);
            if (DbEnum.class.isAssignableFrom(clazz)) {
                log.info("找到数据库枚举：" + className);
                list.add(clazz);
            }
        }
        return list;
    }
}
