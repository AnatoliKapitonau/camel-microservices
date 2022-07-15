package com.learn.microservices.camelmicroservicesa.routes.c;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqSenderRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // timer
        // queue
        from("timer:active-mq-timer?period=10000")
                .transform().constant("My msg for Active MQ")
                .log("${body}")
                .to("activemq:my-activemq-queue");

    }
}
