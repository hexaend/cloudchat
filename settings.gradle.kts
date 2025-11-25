rootProject.name = "cloudchat"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


include("common-files")
include("auth-service")
include("test-service")
include("chat-service")
include("message-service")