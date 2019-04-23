package com.hpu.study_plan.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * 定时配置（可以配置静态定时任务）
 */
@Component
@Configuration
public class SchedulerConfig implements InitializingBean {

    @Value("${quartz.recommend_cron}")
    private String recommendCron;

    private static String RECOMMEND_CRON;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    @Override
    public void afterPropertiesSet() {
        RECOMMEND_CRON = recommendCron;
    }

    //任务工厂
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();

        jobFactory.setApplicationContext(applicationContext);

        return jobFactory;
    }

    //配置medal定时任务
    @Bean
    public JobDetailFactoryBean recommendJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(RecommendJob.class);
        factoryBean.setGroup("medalGroup");
        factoryBean.setName("medalJob");
        return factoryBean;
    }

    @Bean
    public CronTriggerFactoryBean recommendJobTrigger(@Qualifier("recommendJobDetail") JobDetail jobDetail) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();

        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(1000L);
        factoryBean.setName("recommendTrigger");
        factoryBean.setGroup("recommendGroup");
        //每天0点1分0秒触发
        logger.info("RECOMMEND_CRON = " + RECOMMEND_CRON);
        factoryBean.setCronExpression(RECOMMEND_CRON);
        //factoryBean.setCronExpression("0/6 * * * * ?");
        return factoryBean;
    }

    //基础配置
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();

        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));

        propertiesFactoryBean.afterPropertiesSet();

        return propertiesFactoryBean.getObject();
    }

    //任务工厂，负责加载任务bean
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, Trigger recommendJobTrigger)
            throws IOException{
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(recommendJobTrigger);

        return factory;
    }

}

