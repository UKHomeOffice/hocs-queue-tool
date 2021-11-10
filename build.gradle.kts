import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
}

group = "uk.gov.digital.ho.hocs"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.5.5")

	implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.amazonaws:amazon-sqs-java-messaging-lib:1.0.8")
	implementation("com.google.code.gson:gson:2.8.9")

	testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.5.5")

	testImplementation("org.awaitility:awaitility-kotlin:4.1.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.5")

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
