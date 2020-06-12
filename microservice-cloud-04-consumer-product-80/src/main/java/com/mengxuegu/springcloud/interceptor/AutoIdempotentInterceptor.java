package com.mengxuegu.springcloud.interceptor;

import com.mengxuegu.springcloud.service.AutoIdempotent;
import com.mengxuegu.springcloud.service.TokenService;
import com.netflix.discovery.converters.Auto;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;


/**
 * @author HuangCheng
 * @date 2020/3/28 12:47
 * @desc: 拦截器
 */

@Component
public class AutoIdempotentInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;

    /**
     * 预处理
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //本AutoIdempotent标记的扫描
        AutoIdempotent annotation = method.getAnnotation(AutoIdempotent.class);
        if(annotation !=null){
            try {
                return tokenService.checkToken(request);
            } catch (Exception e) {
                e.printStackTrace();
                String message = e.getMessage();
                writeReturnJson(response,message);
            }
        }
        //必须返回true，否则会被拦截一切请求。
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
    private void writeReturnJson(HttpServletResponse response,String json){
        PrintWriter printWriter = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        try {
            printWriter = response.getWriter();
            printWriter.print(json);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (printWriter!=null){
                printWriter.close();
            }
        }
    }
}

