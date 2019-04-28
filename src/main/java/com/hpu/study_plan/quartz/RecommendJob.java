package com.hpu.study_plan.quartz;

import com.hpu.study_plan.service.RecommendService;
import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 自定义定时任务
 */
@Component
public class RecommendJob implements Job, InitializingBean {

    @Value("${quartz.recommend_run}")
    private String quartzRun;
    @Value("${quartz.global_run}")
    private String globalRun;
    private static final Logger logger= Logger.getLogger(RecommendJob.class);
    private static String QUARTZ_RUN;
    private static String GLOBAL_RUN;
    @Override
    public void afterPropertiesSet() {
        QUARTZ_RUN = quartzRun;
        GLOBAL_RUN = globalRun;
    }

    @Autowired
    RecommendService recommendService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        if (GLOBAL_RUN.equals("true") && QUARTZ_RUN.equals("true")) {
            logger.info("用户热门推荐任务开始");
            recommendService.insertHotData2Redis();
            logger.info("用户热门推荐任务结束");
            logger.info("用户个性推荐任务开始");
            recommendService.insertUserItemCF2Redis();
            logger.info("用户个性推荐任务结束");
        } else {
            logger.info("用户推荐任务未执行，请注意");
        }
    }
}
