package com.edoctor.api.configuration.database

import com.zaxxer.hikari.HikariDataSource
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories("com.edoctor.api.repositories")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
class DatabaseConfiguration {

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean = LocalContainerEntityManagerFactoryBean().apply {
        dataSource = dataSource()
        jpaVendorAdapter = HibernateJpaVendorAdapter()
        persistenceUnitName = "default"
        setPackagesToScan("com.edoctor.api.entities.storage");
    }

    @Bean
    fun dataSource(): DataSource = HikariDataSource().apply {
        maximumPoolSize = 100
        jdbcUrl = "jdbc:mysql://localhost:3306/?serverTimezone=UTC"
        addDataSourceProperty("user", "root")
        addDataSourceProperty("password", "rootroot")
        addDataSourceProperty("cachePrepStmts", true)
        addDataSourceProperty("prepStmtCacheSize", 250)
        addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        addDataSourceProperty("useServerPrepStmts", true)
    }

    @Bean
    fun transactionManager(): PlatformTransactionManager =
            JpaTransactionManager().also {
                it.entityManagerFactory = entityManagerFactory().`object`
            }

//    @Bean
//    fun getSessionFactory(): SessionFactory =
//            entityManagerFactory().`object`!!.unwrap(SessionFactory::class.java)
//                    ?: throw NullPointerException("factory is not a hibernate factory")

}