import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("java")
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "net.lumue.filewalkerd"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
    applicationName="filewalkerd"
    mainClass.set("net.lumue.filewalkerd.FilewalkerdApplicationKt")
}

val springBootVersion: String get() = "2.5.4"
val dockerHubPassword: String get() = "M9w8a+ET9u@+tA%"
val dockerHubUser: String get() = "lumue"

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

extra["springBootAdminVersion"] = "2.3.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-quartz:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    developmentOnly("org.springframework.boot:spring-boot-devtools:$springBootVersion")

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
    implementation("com.github.lumue:nfotools:1.11-RELEASE")
    implementation("com.github.lumue:infojsontools:-SNAPSHOT")
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
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")



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

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    docker {

        imageName = "lumue/${application.applicationName}"
        isPublish=true
        publishRegistry {
            username = dockerHubUser
            password = dockerHubPassword
            url = "https://registry.docker-hub.com/v2/"
            email = "mueller.lutz@gmail.com"
        }
    }
}