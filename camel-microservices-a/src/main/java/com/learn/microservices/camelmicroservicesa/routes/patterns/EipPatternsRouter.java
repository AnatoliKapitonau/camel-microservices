package com.learn.microservices.camelmicroservicesa.routes.patterns;

import com.learn.microservices.camelmicroservicesa.model.CurrencyExchange;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//@Component
public class EipPatternsRouter extends RouteBuilder {

    @Autowired
    private SplitterComponent splitterComponent;

    @Autowired
    private DynamicRouter dynamicRouter;

    @Override
    public void configure() throws Exception {

        getContext().setTracing(true);

        errorHandler(deadLetterChannel("activemq:dead-letter-queue"));

        // Pipeline

        // Content Based Routing - choice()

        // Multicast
//        from("timer:multicast?period=10000")
//                .multicast()
//                .to("log:something1", "log:something2", "log:something3");

//        from("file:files/csv")
//                .unmarshal().csv()
//                .split(body())
//                .to("activemq:split-queue");

//        from("file:files/csv")
//                .convertBodyTo(String.class)
//                .split(method(splitterComponent))
////                .split(body(), ",")
//                .to("activemq:split-queue");

        // Aggregate
        // Messages => Aggregate => Endpoint
        // to , 3

        from("file:files/aggregate-json")
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .aggregate(simple("${body.to}"), new ArrayListAggregationStrategy())
                .completionSize(3)
//                .completionTimeout(HIGHEST)
                .to("log:aggregate-json");


        // Routing Slip
        String routingSlip = "direct:endpoint1, direct:endpoint3";
//        String routingSlip = "direct:endpoint1, direct:endpoint2, direct:endpoint3";

        // Dynamic Routing

        // Step 1, Step 2, Step 3
        from("timer:routingSlip?period={{time-period}}")
                .transform().constant("My msg is hardcoded")
                .dynamicRouter(method(dynamicRouter));

        // Endpoint 1
        // Endpoint 2
        // Endpoint 3



        from("direct:endpoint1")
                .wireTap("log:wire-tap")// add
                .log("{{endpoint-for-logging}}");

        from("direct:endpoint2")
                .log("log:directendpoint2");

        from("direct:endpoint3")
                .log("log:directendpoint3");
    }
}

@Component
class SplitterComponent {
    public List<String> splitInput (String body) {
        return List.of("ABC", "DEF", "GHI");
    }
}

@Component
class DynamicRouter {
    Logger logger = LoggerFactory.getLogger(DynamicRouter.class);

    int invocation;

    public String decideTheNextEndpoint (@ExchangeProperties Map<String, String> properties,
                                         @Headers Map<String, String> headers,
                                         @Body String body) {
        logger.info("{} {} {}", properties, headers, body);
        invocation++;

        if (invocation % 3 == 0)
            return "direct:endpoint1";

        if (invocation % 3 == 1)
            return "direct:endpoint2,direct:endpoint3";

        return null;
    }
}