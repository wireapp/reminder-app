//import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.allopen") version "2.1.20"
    kotlin("plugin.noarg") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
//    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
//    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("io.quarkus")
}

repositories {
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

// todo move to catalog, ok for now
dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-quartz")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-kotlin-serialization")
    implementation("io.quarkus:quarkus-rest-client-reactive-kotlin-serialization")
    implementation("io.quarkus:quarkus-websockets-client")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("com.rubiconproject.oss:jchronic:0.2.8")
    implementation("io.github.yamilmedina:natural-kron:2.0.0")
    implementation("io.arrow-kt:arrow-core:1.2.0-RC")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    implementation("com.wire:wire-apps-jvm-sdk:0.0.1")
}

group = "com.wire.bots"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

//ktlint {
//    verbose.set(true)
//    outputToConsole.set(true)
//    coloredOutput.set(true)
//    reporters {
//        reporter(ReporterType.CHECKSTYLE)
//        reporter(ReporterType.JSON)
//        reporter(ReporterType.HTML)
//    }
//}
//
//detekt {
//    toolVersion = "1.23.7"
//    config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
//    baseline = file("$rootDir/config/detekt/baseline.xml")
//    parallel = true
//    buildUponDefaultConfig = true
//    source.setFrom("src/main/kotlin")
//}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        javaParameters.set(true)
    }
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
//    kotlinOptions.javaParameters = true
//}
