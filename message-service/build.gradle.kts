version = "0.0.1-SNAPSHOT"

plugins {
    id("org.springframework.boot")
}

dependencies {

    // Web Dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    //  Security Dependencies
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.confluent:kafka-avro-serializer:8.1.0")

    implementation(project(":common-files"))

    // JPA and Database Dependencies

    // Other

    // Test Dependencies
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}