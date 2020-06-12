package com.mengxuegu.springcloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class LoginFilter extends ZuulFilter {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public String filterType() {
        return "pre";//请求路由器前调用
    }

    @Override
    public int filterOrder() {
        return 1;//int类型的值来定义过滤器的执行顺序，数字越小优先级越高
    }

    @Override
    public boolean shouldFilter() {
        return true;//该过滤器是否执行，true执行，false不执行
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //获取请求参数token的值
        String token = request.getParameter("token");
        if(token==null){
            logger.warn("此操作之前需要先登录");
            currentContext.setSendZuulResponse(false);//拒绝访问
            currentContext.setResponseStatusCode(200);//设置响应状态码
            try {
                currentContext.getResponse().getWriter().write("token is null");
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
        logger.info("ok");
        return null;
    }
}
