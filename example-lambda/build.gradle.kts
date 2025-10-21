plugins {
    id("kotlin-conventions")
    id("testing-conventions")
//    id("dokka-conventions")
//  id("publishing-conventions") // If everything was configured correctly, you could use it to publish the artifacts. But it is not working with Spring as I thought.
    id("spring-conventions")
    id("native-conventions")
}

dependencies {
    implementation("io.github.crac:org-crac:0.1.3")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")
    implementation(project(":example-domain"))
    implementation(project(":example-outbound"))
//    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws")

    implementation(platform(libs.springCloudAwsDependencies.bom.get().toString()))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-dynamodb")
    implementation(libs.bundles.aws.lambda)

    implementation("com.amazonaws:aws-xray-recorder-sdk-core")
    implementation("com.amazonaws:aws-xray-recorder-sdk-apache-http")
    implementation("com.amazonaws:aws-xray-recorder-sdk-aws-sdk")
    implementation("com.amazonaws:aws-xray-recorder-sdk-aws-sdk-instrumentor")
    implementation("com.amazonaws:aws-xray-recorder-sdk-aws-sdk-v2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom(libs.springCloud.bom.get().toString())
        mavenBom(libs.aws.xray.bom.get().toString())
        mavenBom(libs.aws.sdk.bom.get().toString())
    }
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.addAll(
                "--enable-url-protocols=http",
                "-H:+AddAllCharsets"
            )
        }
    }
}