plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

dependencies {
    implementation(project(":core:wallet-application"))
    implementation(project(":adapter:wallet-external-api"))
    implementation(project(":adapter:wallet-persistence"))
    implementation(project(":adapter:wallet-kafka"))
    implementation(libs.spring.boot.actuator)

    runtimeOnly(libs.h2)
}

tasks.bootJar {
    archiveFileName.set("point-service.jar") // 생성될 jar 파일명
}

tasks.jar {
    enabled = false
}

jib {
    from {
        image = "eclipse-temurin:21-jre-alpine" // 베이스 이미지
    }
    to {
        image = "point-service"
        tags = setOf("latest")
    }
    container {
        mainClass = "com.wallet.point.PointApplicationKt"
        ports = listOf("8080")
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
        environment =
            mapOf(
                "TZ" to "Asia/Seoul",
            )
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}
