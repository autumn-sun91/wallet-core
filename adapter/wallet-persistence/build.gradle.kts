plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    implementation(project(":core:wallet-domain"))
    implementation(project(":core:wallet-application"))
    implementation(libs.spring.boot.data.jpa)
    implementation(libs.mysql.connector)
    implementation(libs.hypersistence.utils)
}
