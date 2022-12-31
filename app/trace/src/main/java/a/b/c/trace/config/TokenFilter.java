package a.b.c.trace.config;

import a.b.c.base.util.StringUtil;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.cache.UserInfoCache;
import a.b.c.trace.mapper.UserInfoMapper;
import a.b.c.trace.model.UserInfo;
import a.b.c.trace.model.vo.Token;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@WebFilter(urlPatterns = {"/**"},filterName = "tokenAuthorFilter")
public class TokenFilter implements Filter {

    @Resource
    UserInfoCache userInfoCache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TokenFilter init {}",filterConfig.getFilterName());
    }

    private List<String> whiteList= Arrays.asList("/stomp/websocketJS/info");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException, IOException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        if(request.getRequestURI().startsWith("/stomp/websocketJS")){
            chain.doFilter(request,response);
            return;
        }

        String tokenJs=request.getHeader("token");
        if(StringUtil.isEmpty(tokenJs) || !tokenJs.startsWith("{")){
            log.info("非法请求:"+request.getRequestURI());
            return;
        }
        try{
            Token token= JsonUtil.toBean(tokenJs, Token.class);
            UserInfo user=userInfoCache.get(token.getName());
            if(user==null){
                response.getWriter().write("null");
                return;
            }
            if(!user.getPwd().equalsIgnoreCase(token.getPwd())){
                response.getWriter().write("wrong");
                return;
            }
        }catch (Exception ex){
            if(ex instanceof JsonMappingException){
                response.getWriter().write("json");
                return;
            }
            log.error("权限验证失败",ex);
            response.getWriter().write("error");
            return;
        }
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        log.info("TokenFilter destroy");
    }

}