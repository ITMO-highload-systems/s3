plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

// Dependencies
val minioVersion = "8.5.11"
val testContainersVersion = "1.20.1"
val flyWayVersion = "10.10.0"
val postgresqlVersion = "42.7.3"
val jacksonVersion = "2.17.2"
val kotlinJetBrainsVersion = "2.0.20"
val r2dbcVersion = "1.0.7.RELEASE"
val reactorVersion = "3.6.11"
val cloudVersion = "4.1.3"
val wiremockVersion = "3.9.2"
val reactiveFeignVersion = "4.2.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.data:spring-data-r2dbc")
    implementation("org.postgresql:r2dbc-postgresql:$r2dbcVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.flywaydb:flyway-core:$flyWayVersion")
    implementation("io.minio:minio:$minioVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$cloudVersion")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:$reactiveFeignVersion")

    // Runtime dependencies
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flyWayVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.1")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:minio:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("org.testcontainers:r2dbc:$testContainersVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinJetBrainsVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// Test task configuration
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    withType<JavaCompile>
    {
        dependsOn(processResources)
    }
}