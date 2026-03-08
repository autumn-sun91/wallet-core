dependencies {
    implementation(project(":core:wallet-application"))
    implementation(libs.spring.boot.kafka)
    implementation(libs.spring.boot.retry)
    implementation(libs.jackson.kotlin)
}
