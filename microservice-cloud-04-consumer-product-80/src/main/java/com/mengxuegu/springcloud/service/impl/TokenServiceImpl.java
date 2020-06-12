package com.mengxuegu.springcloud.service.impl;

import com.mengxuegu.springcloud.constant.Constant;
import com.mengxuegu.springcloud.service.RedisService;
import com.mengxuegu.springcloud.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import java.util.UUID;

/**
 * @author HuangCheng
 * @date 2020/3/28 12:20
 * @desc: token服务实现类
 */

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisService redisService;

    /**
     * 创建token
     * @return
     */
    @Override
    public String createToken() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        StringBuilder token = new StringBuilder();
        try {
            token.append(Constant.TOKEN_PREFIX).append(str);
            redisService.setEx(token.toString(),token.toString(),1000L);
            boolean flag = StringUtils.isEmpty(token.toString());
            if(!flag){
                return token.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验token
     * @param request
     * @return
     */
    @Override
    public boolean checkToken(HttpServletRequest request) throws Exception {
        String token = request.getHeader(Constant.TOKEN_NAME);
        if(StringUtils.isEmpty(token)){//header中不存在token
            token = request.getParameter(Constant.TOKEN_NAME);
            if(StringUtils.isEmpty(token)){//parameter中也不存在token
                throw new Exception("不存在token!");
            }
        }
        /*if(!redisService.exists(token)){
            throw new Exception("不存在token!");
        }*/
        boolean remove = redisService.remove(token);
        if(!remove){
            throw new Exception("不存在token!");
        }
        return true;
    }

}
