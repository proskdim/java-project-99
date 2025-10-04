import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    jacoco
    alias(libs.plugins.lombok)
    alias(libs.plugins.versions)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.shadow)
    alias(libs.plugins.sonarqube)
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "App project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // Spring Boot
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterValidation)
    implementation(libs.springBootStarterActuator)
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springBootStarterOauth2ResourceServer)
    implementation(libs.springBootDevtools)
    implementation(libs.springBootConfigProcessor)

    // DB
    implementation(libs.postgresql)
    runtimeOnly(libs.h2)

    //tools
    implementation(libs.jacksonDatabindNullable)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstructProcessor)

    // Tests
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.springSecurityTest)
    testImplementation(libs.instancioJunit)
    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testImplementation(libs.jsonunitAssertj)
    testRuntimeOnly(libs.junitPlatformLauncher)

    // swagger
    implementation(libs.springdocOpenapiUi)

    implementation(libs.datafaker)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
        showStandardStreams = true
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        eclipse().sortMembersEnabled(true)
        formatAnnotations()
        leadingTabsToSpaces(4)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "proskdim_java-project-99")
        property("sonar.organization", "proskdim")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

application {
    mainClass.set("hexlet.code.app.AppApplication")
}
