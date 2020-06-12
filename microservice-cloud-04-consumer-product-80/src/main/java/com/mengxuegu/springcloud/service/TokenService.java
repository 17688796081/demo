package com.mengxuegu.springcloud.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HuangCheng
 * @date 2020/3/28 12:17
 * @desc: token服务接口
 */
public interface TokenService {
    /**
     * 创建token
     * @return
     */
    public String createToken();

    /**
     * 校验token
     * @param request
     * @return
     */
    public boolean checkToken(HttpServletRequest request) throws Exception;
}
