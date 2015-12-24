package com.jshipper.acled.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jshipper.acled.dao.ConflictDao;
import com.jshipper.acled.dao.ConflictDaoImpl;
import com.jshipper.acled.service.ConflictService;
import com.jshipper.acled.service.ConflictServiceImpl;

/**
 * Configures transaction management and needed beans for ConflictService
 * 
 * @author jshipper
 *
 */
@Configuration
@EnableTransactionManagement
public class ConflictServiceConfig {
  @Bean
  public ConflictService conflictService(ConflictDao dao) {
    return new ConflictServiceImpl(dao);
  }

  @Bean
  public ConflictDao conflictDao(SessionFactory sessionFactory) {
    return new ConflictDaoImpl(sessionFactory);
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer
    propSrcPlaceholderConfig() {
    PropertySourcesPlaceholderConfigurer propSrcPlaceholderConfig =
      new PropertySourcesPlaceholderConfigurer();
    propSrcPlaceholderConfig.setIgnoreUnresolvablePlaceholders(false);
    return propSrcPlaceholderConfig;
  }

  @Bean
  public DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl,
    @Value("${jdbc.username}") String jdbcUsername,
    @Value("${jdbc.password}") String jdbcPassword,
    @Value("${jdbc.driver.classname}") String jdbcDriverClassname) {
    BasicDataSource datasource = new BasicDataSource();
    datasource.setUrl(jdbcUrl);
    datasource.setUsername(jdbcUsername);
    datasource.setPassword(jdbcPassword);
    datasource.setDriverClassName(jdbcDriverClassname);
    return datasource;
  }

  @Bean(name = "hibernateProps")
  public Properties hibernateProps(
    @Value("${hibernate.dialect}") String hibernateDialect,
    @Value("${create.schema:false}") boolean createSchema) {
    Properties props = new Properties();
    // Hibernate properties
    props.put("hibernate.dialect", hibernateDialect);
    if (createSchema) {
      props.put("hibernate.hbm2ddl.auto", "create");
    }
    return props;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory(
    @Qualifier("hibernateProps") Properties props, DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan("com.jshipper.acled.model");
    sessionFactory.setHibernateProperties(props);
    return sessionFactory;
  }

  @Bean
  public PlatformTransactionManager txManager(SessionFactory sessionFactory,
    DataSource datasource) {
    HibernateTransactionManager txManager = new HibernateTransactionManager();
    txManager.setSessionFactory(sessionFactory);
    txManager.setDataSource(datasource);
    return txManager;
  }
}
