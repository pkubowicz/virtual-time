import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.10"
	id("org.jmailen.kotlinter") version "3.7.0"
	id("com.github.ben-manes.versions") version "0.39.0"
}

group = "com.nexocode"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	api(platform("io.projectreactor:reactor-bom:2020.0.14"))
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.apache.logging.log4j:log4j-core:2.17.0")

	testImplementation(platform("org.junit:junit-bom:5.8.2"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.assertj:assertj-core:3.21.0")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
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

kotlinter {
	disabledRules = arrayOf(
		"no-wildcard-imports",
	)
}
