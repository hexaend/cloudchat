version = "0.0.1-SNAPSHOT"

plugins {
    java
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}


dependencies {
    implementation("io.confluent:kafka-avro-serializer:8.1.0")
}