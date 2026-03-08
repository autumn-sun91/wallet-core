
dependencies {
    implementation(project(":core:wallet-domain"))
    implementation(project(":core:wallet-application"))
    implementation(libs.spring.boot.web)
    implementation(libs.jackson.kotlin)
}
