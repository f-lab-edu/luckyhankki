package com.java.luckyhankki.config.batch;

import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Profile("!test")
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ProductRepository productRepository;
    private final DataSource dataSource;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       ProductRepository productRepository,
                       @Qualifier("sourceDataSource") DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.productRepository = productRepository;
        this.dataSource = dataSource;
    }

    /**
     * Batch job 정의
     */
    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
                .start(batchStep())
                .build();
    }

    /**
     * Batch Step 정의: 청크 기반 처리 (10개씩)
     */
    @Bean
    public Step batchStep() {
        return new StepBuilder("batchStep", jobRepository)
                .<Product, Product> chunk(100, transactionManager)
                .reader(reader(LocalDateTime.now())) //임의의 값을 호출 시점에 적용
                .writer(writer())
                .build();
    }

    /**
     * ProductRepository로 데이터 조회
     * @param now 호출된 값을 Job Parameter가 Override
     */
    @Bean
    @StepScope
    public RepositoryItemReader<Product> reader(@Value("#{jobParameters['now']}") LocalDateTime now) {
        log.info("reader() - now:{} ", now);

        return new RepositoryItemReaderBuilder<Product>()
                .name("reader")
                .pageSize(1000)
                .methodName("findAllByPickupEndDateTimeBeforeAndIsActiveTrue")
                .arguments(List.of(now))
                .repository(productRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    /**
     * Source DB에 저장
     */
    @Bean
    public JdbcBatchItemWriter<Product> writer() {
        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(dataSource)
                .sql("UPDATE product SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE product_id = ?")
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setLong(1, item.getId());
                })
                .build();
    }
}
