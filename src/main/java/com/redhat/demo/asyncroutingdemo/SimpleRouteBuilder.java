package com.redhat.demo.asyncroutingdemo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.sqs.Sqs2Constants;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


//@Component
public class SimpleRouteBuilder extends RouteBuilder {

    private final Environment env;

    public SimpleRouteBuilder(Environment env) {
        this.env = env;
    }

    @Override
    public void configure() throws Exception {
    	
    //	BasicDataSource basic = new BasicDataSource();
    //	basic.setDriverClassName("cdata.jdbc.mysql.MySQLDriver");
    	//basic.setUrl("jdbc:mysql:User=myUser;Password=myPassword;Database=NorthWind;Server=myServer;Port=3306;");
    	
//    	AWSCredentials awsCredentials = new BasicAWSCredentials("myAccessKey", "mySecretKey");
//
//    	ClientConfiguration clientConfiguration = new ClientConfiguration();
//    	clientConfiguration.setProxyHost("http://myProxyHost");
//    	clientConfiguration.setProxyPort(8080);
//
//    	AmazonSQS client = new AmazonSQSClient(awsCredentials, clientConfiguration);
//
//    	registry.bind("client", client);
//    	from("aws-sqs://MyQueue?amazonSQSClient=#client&delay=5000&maxMessagesPerPoll=5")
//    	.to("mock:result");
    	
        onException(Exception.class).process(new Processor() {

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
       // .wireTap("direct:wiretap-quotelo-req")
       .choice()
            .when(header("client").isNotNull())
                .toD("atlasmap:maps/${header.client}.adm")
                .convertBodyTo(String.class)
                .to("log:CF-outgoing-request")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("https://lgi-backend-git-lgi-poc-quote.apps.cluster-fzgbp.fzgbp.sandbox1096.opentlc.com/Motor?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .to("aws2-sqs://camelqueue?accessKey=RAW(AKIAUFWMQGIC3FJFQPXQ)&secretKey=RAW(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)&region=ap-south-1")
            .otherwise()
                .convertBodyTo(String.class)
                .to("log:OW-outgoing-request")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("https://lgi-backend-git-lgi-poc-quote.apps.cluster-fzgbp.fzgbp.sandbox1096.opentlc.com/Motor?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .to("aws2-sqs://camelqueue?accessKey=RAW(AKIAUFWMQGIC3FJFQPXQ)&secretKey=RAW(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)&region=ap-south-1");
        
        
//        from("direct:start")
//
//         .setHeader(Sqs2Constants.SQS_OPERATION, constant("purgeQueue"))
//
//        .setHeader(Sqs2Constants.SQS_OPERATION, constant("listQueues"))
//
//        .to("aws2-sqs://camelqueue?accessKey=RAW(AKIAUFWMQGICW7MUHCER)&secretKey=RAW(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)&region=ap-south-1");



 }

//        from("direct:HttpFailed")
//        .wireTap("direct:wiretap-log")
//        .to("language:constant:Bad Request");
//
//        from("direct:wiretap-log")
//        .convertBodyTo(String.class)
//        .to("log: ---- EXCEPTION ----");
        
//        from("direct:start")
//        .setBody(constant("Camel rocks!"))
//        .to("aws2-sqs://camelqueue?accessKey=RAW(AKIAUFWMQGIC3FJFQPXQ)&secretKey=RAW(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)&region=ap-south-1");


    
}

