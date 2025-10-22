package com.wtech

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.xray.entities.TraceHeader
import com.amazonaws.xray.entities.TraceID
import com.amazonaws.xray.interceptors.TracingInterceptor
import com.wtech.ecommerce.domain.product.model.Product
import org.joda.time.DateTime
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints
import java.util.HashSet

@SpringBootApplication
@ImportRuntimeHints(Application.CoreRuntimeHints::class)
class Application{
    class CoreRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            hints.reflection()
                .registerType(DateTime::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerType(APIGatewayProxyRequestEvent::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerType(TracingInterceptor::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerType(HashSet::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerType(TraceHeader::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerType(TraceID::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerType(Product::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
//                .registerType(Products::class.java, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)

            hints.reflection()
                .registerTypeIfPresent(classLoader, "com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent\$ProxyRequestContext", MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerTypeIfPresent(classLoader, "com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent\$RequestIdentity", MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerTypeIfPresent(classLoader, "com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent\$LogsForRequestIdentity", MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
                .registerTypeIfPresent(classLoader, "com.amazonaws.xray.entities.TraceHeader\$SampleDecision")
        }
    }
}

fun main(vararg args: String) {
    runApplication<Application>(*args)
}
