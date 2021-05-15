import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "io.github.lumue.filescanner"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

extra["springBootAdminVersion"] = "2.3.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-jersey")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.integration:spring-integration-http")
    implementation("org.springframework.integration:spring-integration-mongodb")
    implementation("org.springframework.integration:spring-integration-webflux")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.integration:spring-integration-test")

    implementation("org.springframework.integration:spring-integration-core")
    implementation("org.springframework.integration:spring-integration-event")
    implementation("org.springframework.integration:spring-integration-file")
    // json serializer
    implementation("com.google.guava:guava:12.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.10.4")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")


    implementation("org.springdoc:springdoc-openapi-ui:1.5.2")


    //tika parser
    implementation("org.apache.tika:tika-core:1.25")
    implementation("org.apache.tika:tika-parsers:1.25")

    //mp4parser
    implementation ("org.mp4parser:isoparser:1.9.41")

    implementation("io.swagger:swagger-annotations:1.5.22")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.4")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.4")
    implementation("org.openapitools:jackson-databind-nullable:0.2.1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("javax.ws.rs:javax.ws.rs-api:2.1.1")

    //nfo
    implementation("com.github.lumue:nfotools:1.10-RELEASE")
    implementation("com.github.lumue:infojsontools:master-SNAPSHOT")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.4")

    //mdresolver
    //implementation ("com.github.lumue:mdresolver-client-lib:0.1-RELEASE")

    implementation("commons-io:commons-io:2.4")

//  logging
    implementation("org.slf4j:slf4j-api:1.7.18")

//   https://mvnrepository.com/artifact/org.aspectj/aspectjweaver
    implementation("org.aspectj:aspectjweaver:1.9.1")
//
//  //test
    testImplementation("junit:junit:4.11")
//
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
