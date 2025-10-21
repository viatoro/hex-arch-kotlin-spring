plugins {
    id("kotlin-conventions")
    id("testing-conventions")
//    id("dokka-conventions")
//  id("publishing-conventions") // If everything was configured correctly, you could use it to publish the artifacts. But it is not working with Spring as I thought.
    id("spring-conventions")
    id("native-conventions")
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

tasks.jar { enabled = true }
tasks.bootJar { enabled = false }

// Disable Spring Boot application tasks for library module
tasks.named("bootBuildImage") { enabled = false }
tasks.matching { it.name == "bootRun" }.configureEach { enabled = false }
tasks.matching { it.name == "processAot" }.configureEach { enabled = false }
tasks.matching { it.name == "processTestAot" }.configureEach { enabled = false }

// Disable native compilation tasks for library module
tasks.matching { it.name == "nativeBuild" }.configureEach { enabled = false }
tasks.matching { it.name == "nativeCompile" }.configureEach { enabled = false }
tasks.matching { it.name == "nativeTestCompile" }.configureEach { enabled = false }
tasks.matching { it.name == "nativeTestBuild" }.configureEach { enabled = false }

//dependencyManagement {
//    imports {
//        mavenBom(libs.springCloud.bom.get().toString())
//        mavenBom(libs.aws.xray.bom.get().toString())
//        mavenBom(libs.aws.sdk.bom.get().toString())
//    }
//}

