package exam.veracityid;

import liquibase.integration.spring.SpringLiquibase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class RepositoryConfig {

    @Value("${spring.jpa.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${spring.jpa.hibernate.hbm2ddl.auto}")
    private String hibernatHbm2ddlAuto;

    @Value("${spring.jpa.hibernate.show_sql}")
    private String hibernateShowSql;

    @Value("${spring.jpa.hibernate.format_sql}")
    private String hibernateFormatSql;

    @Value("${liquibase.change-log}")
    private String liquibaseChangeLog;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    //@Bean
    public LocalContainerEntityManagerFactoryBean emf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("exam.veracityid");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setDataSource(this.dataSource());
        emf.setJpaProperties(this.getJPAProperties());

        return emf;
    }

    private Properties getJPAProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", hibernateDialect);
        properties.setProperty("hibernate.hbm2ddl.auto", hibernatHbm2ddlAuto);
        properties.setProperty("hibernate.show_sql", hibernateShowSql);
        properties.setProperty("hibernate.format_sql", hibernateFormatSql);
        return properties;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(this.dataSource());
        liquibase.setChangeLog(liquibaseChangeLog);
        return liquibase;
    }

}