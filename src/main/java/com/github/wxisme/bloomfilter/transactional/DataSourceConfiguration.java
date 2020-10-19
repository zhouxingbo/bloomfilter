package com.github.wxisme.bloomfilter.transactional;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 事物未生效，后续得继续研究
 */
@Configuration
public class DataSourceConfiguration {

    @Bean(name = "userDatasource")
    @ConfigurationProperties(prefix = "spring.user-datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(@Qualifier("userDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "nameUserJdbcTemplate")
    public NamedParameterJdbcTemplate nameUserJdbcTemplate(@Qualifier("userDatasource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "userManager")
    public PlatformTransactionManager txManager(@Qualifier("userDatasource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("txAdvice")
    public TransactionInterceptor txAdvice(@Qualifier("userManager") PlatformTransactionManager userManager){

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED );
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
                Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setTimeout(5);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("insert*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        source.setNameMap( txMap );
        return new TransactionInterceptor(userManager ,source) ;
    }

    /**切面拦截规则 参数会自动从容器中注入*/
    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(@Qualifier("txAdvice") TransactionInterceptor txAdvice) {
        DefaultPointcutAdvisor pointcutAdvisor = new DefaultPointcutAdvisor();
        pointcutAdvisor.setAdvice(txAdvice);
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution (* com.zzcm.*.service..*.*(..))");
        pointcutAdvisor.setPointcut(pointcut);
        return pointcutAdvisor;
    }

}
