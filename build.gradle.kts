import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    java
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "net.lumue.filewalkerd"
version = "0.0.1-SNAPSHOT"
java{
    toolchain{
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


application {
    applicationName="filewalkerd"
    mainClass.set("net.lumue.filewalkerd.FilewalkerdApplicationKt")
}

val springBootVersion: String get() = "3.1.3"
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
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.15.2")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")


    implementation("org.springdoc:springdoc-openapi-ui:1.7.0")


    //tika parser
    implementation("org.apache.tika:tika-core:2.9.0")
    implementation("org.apache.tika:tika-parsers:2.9.0")

    //mp4parser
    implementation ("org.mp4parser:isoparser:1.9.41")

    implementation("io.swagger:swagger-annotations:1.5.22")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("org.openapitools:jackson-databind-nullable:0.2.1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("javax.ws.rs:javax.ws.rs-api:2.1.1")

    //nfo
    implementation("com.github.lumue:nfotools:1.11-RELEASE")
    implementation("com.github.lumue:infojsontools:-SNAPSHOT")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    //mdresolver
    //implementation ("com.github.lumue:mdresolver-client-lib:0.1-RELEASE")
//
//    implementation ("org.bytedeco:javacv:1.5.4")
//    implementation ("org.bytedeco.javacpp-presets:opencv:4.3.0-1.5.4")


    implementation("commons-io:commons-io:2.13.0")

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
        freeCompilerArgs = listOf("-Xjsr305=strict ")
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile>{
    val compilerArgs = options.compilerArgs
    compilerArgs.addAll(listOf("-Xlint:deprecation"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    docker {
        imageName.set("lumue/${application.applicationName}")
        publish.set(true)
        publishRegistry {
            username.set(dockerHubUser)
            password.set(dockerHubPassword)
            url.set("https://registry.docker-hub.com/v2/")
            email.set("mueller.lutz@gmail.com")
        }
    }
}