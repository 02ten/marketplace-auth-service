package com.auth.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.opentracing.Tracer;

@Configuration
public class JaegerConfiguration {
    @Bean
    public Tracer jaegerTracer(){
        return new io.jaegertracing.Configuration("AuthService")
                .withSampler(new io.jaegertracing.Configuration.SamplerConfiguration().withType("const").withParam(1))
                .withReporter(new io.jaegertracing.Configuration.ReporterConfiguration()
                        .withLogSpans(true)
                        .withSender(new io.jaegertracing.Configuration.SenderConfiguration()
                                .withEndpoint("http://jaeger.jaeger:14268/api/traces"))) // HTTP endpoint
                .getTracer();
    }
}
