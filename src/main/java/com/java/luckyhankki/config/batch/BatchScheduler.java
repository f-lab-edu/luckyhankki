package com.java.luckyhankki.config.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Profile("!test")
@Configuration
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job batchJob;

    public BatchScheduler(JobLauncher jobLauncher, Job batchJob) {
        this.jobLauncher = jobLauncher;
        this.batchJob = batchJob;
    }

    //TODO 픽업 시작시간, 종료시간 5분 단위만 가능하도록 조건 추가
    @Scheduled(cron = "0 */5 * * * *")
    public void runBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(batchJob, new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters());
    }
}
