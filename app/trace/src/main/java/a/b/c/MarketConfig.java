package a.b.c;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
@Slf4j
public class MarketConfig {
    public static boolean test=true;

    public static boolean clientError=false;

    public static String API_KEY;
    public static String SECRET_KEY;
    public static String PASSWORD;

    static {
        File file=new File("/opt/config/traceConfig");
        if(!file.exists()){
            throw new RuntimeException("找不到配置文件："+file.getAbsolutePath());
        }
        String text= FileUtil.readString(file,"UTF-8");
        String[] keys=text.trim().split("\n");
        MarketConfig.API_KEY=keys[0].trim();
        MarketConfig.SECRET_KEY=keys[1].trim();
        MarketConfig.PASSWORD=keys[2].trim();
        if(keys.length>3){
            try{
                test=Boolean.parseBoolean(keys[4].trim());
            }catch (Exception ex){
                log.error(ex.getMessage(),ex);
            }
        }
    }

}
