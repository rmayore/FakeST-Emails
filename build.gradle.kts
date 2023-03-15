import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.2"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
}


group = "fake.st"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	//implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    runtimeOnly("org.springframework.boot:spring-boot-starter-tomcat")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.mockito:mockito-core:2.21.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.majorVersion
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
