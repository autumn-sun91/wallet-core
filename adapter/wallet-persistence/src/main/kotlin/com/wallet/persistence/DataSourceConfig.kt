package com.wallet.persistence

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@Profile("prod")
class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    fun masterDataSource(): HikariDataSource =
        DataSourceBuilder.create().type(HikariDataSource::class.java).build().apply {
            isAutoCommit = false
        }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave.slave-1")
    fun slaveDataSource1(): HikariDataSource =
        DataSourceBuilder.create().type(HikariDataSource::class.java).build().apply {
            isAutoCommit = false
        }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave.slave-2")
    fun slaveDataSource2(): HikariDataSource =
        DataSourceBuilder.create().type(HikariDataSource::class.java).build().apply {
            isAutoCommit = false
        }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave.slave-3")
    fun slaveDataSource3(): HikariDataSource =
        DataSourceBuilder.create().type(HikariDataSource::class.java).build().apply {
            isAutoCommit = false
        }

    @Bean
    fun routingDataSource(): RoutingDataSource {
        val dataSources =
            mapOf(
                RoutingDataSource.DataSourceType.MASTER to masterDataSource(),
                RoutingDataSource.DataSourceType.SLAVE_1 to slaveDataSource1(),
                RoutingDataSource.DataSourceType.SLAVE_2 to slaveDataSource2(),
                RoutingDataSource.DataSourceType.SLAVE_3 to slaveDataSource3(),
            )
        return RoutingDataSource().apply {
            setTargetDataSources(dataSources as Map<Any, Any>)
            setDefaultTargetDataSource(masterDataSource())
        }
    }

    // ─── LazyConnection 으로 트랜잭션 시작 시 결정 ──
    @Bean
    @Primary
    fun dataSource(): LazyConnectionDataSourceProxy =
        LazyConnectionDataSourceProxy(routingDataSource()).apply {
            setDefaultAutoCommit(false)
        }
}
