package com.redhat.demo.asyncroutingdemo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class SimpleRouteBuilder extends RouteBuilder {

    private final Environment env;

    public SimpleRouteBuilder(Environment env) {
        this.env = env;
    }

    @Override
    public void configure() throws Exception {
    	
    
    	
        onException(Exception.class)
        //.handled(true)
      //  .markRollbackOnly()
        .useOriginalMessage()
        .logStackTrace(true)
        .process(new Processor() {

            public void process(Exchange exchange) throws Exception {
                System.out.println("--- EXCEPTION ---");
                System.out.println(exchange.getMessage());
            }
        }).log("Received body ").handled(true)
        .to("language:constant:Bad Request");;

        restConfiguration()
        .component("servlet");

        rest()
        .consumes(MediaType.APPLICATION_JSON_VALUE)
        .consumes(MediaType.APPLICATION_XML_VALUE)
        .produces(MediaType.APPLICATION_JSON_VALUE)

        .post("/motor").to("direct:async-quote");

        

        from("direct:async-quote")
       // .wireTap("direct:wiretap-quotelo-req")//
       .choice()
            .when(header("client").isNotNull())
           // .log("${body}")
                .toD("atlasmap:maps/${header.client}.adm")
                .log("${body}")
                .convertBodyTo(String.class)
                .log("${body}")
                .to("log:CF-outgoing-request")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
               .to("https://lgi-backend-git-lgi-poc-quote.apps.cluster-fzgbp.fzgbp.sandbox1096.opentlc.com/Motor?bridgeEndpoint=true")
                .convertBodyTo(String.class)              
               .to("direct:multicast-parraller-process")
            .otherwise()
                .convertBodyTo(String.class)
                .to("log:OW-outgoing-request")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
               .to("https://lgi-backend-git-lgi-poc-quote.apps.cluster-fzgbp.fzgbp.sandbox1096.opentlc.com/Motor?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .to("direct:multicast-parraller-process");                
 

    		}

 
    
}

