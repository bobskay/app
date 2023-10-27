package a.b.c.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = {"/**"},filterName = "prefixFilter")
public class PrefixFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        String uri =  request.getRequestURI();
        if(uri.startsWith("/api")){
            uri=uri.replace("/api","");
        }
        request.getRequestDispatcher(uri).forward(request, response);
    }
}