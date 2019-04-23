package com.hpu.study_plan.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("unchecked")
@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    private static final String USER_PREFIX = GlobalPropertyUtils.get("redis.user.key.prefix");
    private static final String PHONE_CODE_PREFIX = GlobalPropertyUtils.get("redis.phone_code.key.prefix");
    private static final String RECOMMEND_PREFIX = GlobalPropertyUtils.get("redis.recommend.key.prefix");

    @Autowired
    @Qualifier("stringRedisTemplate")
    StringRedisTemplate defultRedis;

    @Autowired
    @Qualifier("userRedis")
    StringRedisTemplate userRedis;

    @Autowired
    @Qualifier("phoneCodeRedis")
    StringRedisTemplate phoneCodeRedis;

    @Autowired
    @Qualifier("recommendRedis")
    StringRedisTemplate recommendRedis;

    public StringRedisTemplate selectRedis(String key) {

        try {
            key = key.split(":")[0];
            if (USER_PREFIX.equals(key)) {
                return userRedis;
            }
            if (PHONE_CODE_PREFIX.equals(key)) {
                return phoneCodeRedis;
            }
            if (RECOMMEND_PREFIX.equals(key)) {
                return recommendRedis;
            }

        } catch (Exception e) {
            logger.error("selectRedis error key = " + key, e);
        }

        return defultRedis;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            StringRedisTemplate stringRedisTemplate = selectRedis(key);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存  指定时间  单位：秒
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            StringRedisTemplate stringRedisTemplate = selectRedis(key);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        StringRedisTemplate stringRedisTemplate = selectRedis(key);
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        String result = null;
        StringRedisTemplate stringRedisTemplate = selectRedis(key);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        result = operations.get(key);
        return result == null ? "" : result;
    }

    /**
     * 删除对应的key
     *
     * @param key
     */
    public void remove(final String key) {
        StringRedisTemplate stringRedisTemplate = selectRedis(key);
        if (exists(key)) {
            stringRedisTemplate.delete(key);
        }
    }

    public Set<String> keys(String prefix) {
        try {
            StringRedisTemplate stringRedisTemplate = selectRedis(prefix);
            return stringRedisTemplate.keys(prefix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteByPrex(String prefix) {
        try {
            StringRedisTemplate stringRedisTemplate = selectRedis(prefix);
            Set<String> keys = stringRedisTemplate.keys(prefix);
            if (!CollectionUtils.isEmpty(keys)) {
                stringRedisTemplate.delete(keys);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String insertPhoneCode(String key) {
        try {
            String code = randomCode();
            StringRedisTemplate stringRedisTemplate = phoneCodeRedis;
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, code);
            stringRedisTemplate.expire(key, 3600, TimeUnit.SECONDS);
            return code;
        } catch (Exception e) {
            logger.error("insertPhoneCode error key = " + key);
        }

        return "";
    }

    public String getPhoneCode(String key) {
        try {
            StringRedisTemplate stringRedisTemplate = phoneCodeRedis;
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            if (stringRedisTemplate.hasKey(key)) {
                return operations.get(key);
            }
        } catch (Exception e) {
            logger.error("getPhoneCode error key = " + key);
        }

        return "";
    }

    private String randomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


}
