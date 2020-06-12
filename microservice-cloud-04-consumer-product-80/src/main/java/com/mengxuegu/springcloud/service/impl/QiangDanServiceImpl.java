package com.mengxuegu.springcloud.service.impl;

import com.mengxuegu.springcloud.service.QiangDanService;
import com.mengxuegu.springcloud.service.RedisService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author HuangCheng
 * @date 2020/3/29 14:44
 * @desc: 抢单接口实现类
 */

@Component
public class QiangDanServiceImpl implements QiangDanService {


    protected static final Logger logger = Logger.getLogger(QiangDanServiceImpl.class);

    @Autowired
    private RedisService redisService;

    private long nKucuen = 0;//总库存

    private String shangpinKey = "computer_key";//商品key名称

    private int timeout = 30 * 1000;
    /**
     * 模拟抢单
     * @return
     */
    @Override
    public List<String> qiangdan() {
        List<String> list1 = new ArrayList<>();//抢到商品的用户

        List<String> list2 = new ArrayList<>();//构造很多用户

        IntStream.range(0,100000).parallel().forEach(b -> {
            list2.add("神牛_"+b);
        });

        nKucuen = 10;//初始化库存

        //模拟抢单
        list2.parallelStream().forEach(b -> {
            String shopUser = qiang(b);
            if (StringUtils.isEmpty(shopUser)){
                list2.add(shopUser);
            }
        });

        return list2;
    }

    //模拟抢单动作
    private String qiang(String b){
        //用户开抢时间
        long startTime = System.currentTimeMillis();
        //未抢到的情况下，30秒内继续获取锁
        while ((startTime + timeout) >= System.currentTimeMillis()){
            //商品是否剩余
            if(nKucuen <= 0){
                break;
            }
            if(redisService.setnx(shangpinKey,b)){
                //用户b拿到锁
                logger.info("用户拿到锁...");
                try {
                    if (nKucuen <= 0){
                        break;
                    }
                    //模拟生成订单耗时操作
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //抢购成功，商品递减，记录用户
                    nKucuen -= 1;

                    //抢单成功跳出
                    logger.info("用户抢单成功跳出...所剩库存:" + nKucuen);
                    return b + "抢单成功，所剩库存:"+nKucuen;
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    logger.info("用户释放锁...");
                    //释放锁
                    redisService.delnx(shangpinKey,b);
                }
            }else{

            }
        }
        return "";
    }
}
