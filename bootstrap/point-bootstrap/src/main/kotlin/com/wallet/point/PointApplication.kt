package com.wallet.point

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.wallet.point",
        "com.wallet.application",
        "com.wallet.external",
        "com.wallet.persistence",
    ],
)
class PointApplication

fun main(args: Array<String>) {
    runApplication<PointApplication>(*args)
}
