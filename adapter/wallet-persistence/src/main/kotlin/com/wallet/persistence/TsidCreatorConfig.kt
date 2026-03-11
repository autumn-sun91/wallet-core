package com.wallet.persistence

import io.hypersistence.tsid.TSID
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.math.abs

@Configuration
class TsidCreatorConfig {
    @Bean
    fun tsidFactory(): TSID.Factory {
        val hostname = System.getenv("HOSTNAME") ?: "default"
        val node = abs(hostname.hashCode()) % 1024
        return TSID.Factory
            .builder()
            .withNode(node)
            .build()
    }
}
