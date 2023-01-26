package com.redhat.demo.asyncroutingdemo.file.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.sqs.Sqs2Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class EIPRoutePatterns extends RouteBuilder{
	
	@Autowired
	DeciderBean deciderBean;

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
		from("file:files/input")
		.routeId("Files-Input-Route")
		.transform().body(String.class)
		.choice()
		.when(simple("${file:ext} ends with 'xml'"))
			.log("XML file")
		.when(method(deciderBean))
			.log("File contains USD")
		.otherwise()
		 	.log("File not xml")
		 .end()
		 .log("${body}")
		 .to("file:files/output")
		 .to("direct:to_aws-sqs");
		
		
		from("direct:to_aws-sqs")

     //  .setHeader(Sqs2Constants.SQS_OPERATION, constant("purgeQueue"))

     // .setHeader(Sqs2Constants.SQS_OPERATION, constant("listQueues"))

      .to("aws2-sqs://camelqueue?accessKey=RAW(AKIAUFWMQGICW7MUHCER)&secretKey=RAW(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)&region=ap-south-1")
      .log("${body}");
		
	}

}

@Component
class DeciderBean1{
	Logger logger=LoggerFactory.getLogger(DeciderBean.class);
	public boolean isRequestContain(String body) {
		logger.info("Body {}",body);
		
		return true;
	}
}










