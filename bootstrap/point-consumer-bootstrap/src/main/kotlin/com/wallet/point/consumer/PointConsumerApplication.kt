package com.wallet.point.consumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.wallet.point.consumer",
        "com.wallet.application.point",
        "com.wallet.persistence",
        "com.wallet.kafka.point",
    ],
)
class PointConsumerApplication

fun main(args: Array<String>) {
    runApplication<PointConsumerApplication>(*args)
}
