package com.hpu.study_plan.config;

import com.hpu.study_plan.utils.GlobalPropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    private static final int USER_DB = GlobalPropertyUtils.getIntValue("redis.user.db");
    private static final int DEFAULT_DB = GlobalPropertyUtils.getIntValue("redis.default.db");
    private static final int PHONE_CODE_DB = GlobalPropertyUtils.getIntValue("redis.phone_code.db");
    private static final int RECOMMEND_DB = GlobalPropertyUtils.getIntValue("redis.recommend.db");

    @Autowired
    RedisProperties redisProperties;

    @Bean(name = "stringRedisTemplate")
    @Primary
    public StringRedisTemplate stringRedisTemplate() {
        return buildRedisTemplate(buildConnectionFactory(DEFAULT_DB));
    }


    @Bean(name = "userRedis")
    public StringRedisTemplate userRelationRedis() {
        return buildRedisTemplate(buildConnectionFactory(USER_DB));
    }

    @Bean(name = "recommendRedis")
    public StringRedisTemplate recommendRedis() {
        return buildRedisTemplate(buildConnectionFactory(RECOMMEND_DB));
    }

    @Bean(name = "phoneCodeRedis")
    public StringRedisTemplate phoneCodeRedis() {
        return buildRedisTemplate(buildConnectionFactory(PHONE_CODE_DB));
    }

    private LettuceConnectionFactory buildConnectionFactory(int database) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        configuration.setPassword(redisProperties.getPassword());
        configuration.setDatabase(database);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        return factory;
    }

    protected StringRedisTemplate buildRedisTemplate(LettuceConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

}