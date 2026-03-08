plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.jib)
}

dependencies {
    implementation(project(":core:wallet-application"))
    implementation(project(":adapter:wallet-kafka"))
    implementation(project(":adapter:wallet-persistence"))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.actuator)
    implementation(libs.jackson.kotlin)
    runtimeOnly(libs.h2)
    testImplementation(libs.spring.boot.test)
}

tasks.bootJar {
    archiveFileName.set("point-consumer.jar")
}

tasks.jar {
    enabled = false
}

jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
    }
    to {
        image = "point-consumer"
        tags = setOf("latest")
    }
    container {
        mainClass = "com.wallet.consumer.PointConsumerApplicationKt"
        jvmFlags =
            listOf(
                "-Xms256m",
                "-Xmx512m",
                "-XX:+UseContainerSupport",
                "-XX:MaxRAMPercentage=75.0",
                "-Dspring.profiles.active=prod",
                "-Dfile.encoding=UTF-8",
                "-Duser.timezone=Asia/Seoul",
            )
        environment = mapOf("TZ" to "Asia/Seoul")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}
