package com.wallet.persistence.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(basePackages = ["com.wallet.persistence.jpa.point"])
@EnableJpaRepositories(basePackages = ["com.wallet.persistence.jpa.point"])
class JpaConfig
