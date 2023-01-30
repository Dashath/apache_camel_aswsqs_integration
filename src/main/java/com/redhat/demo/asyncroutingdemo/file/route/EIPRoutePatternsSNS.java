package com.redhat.demo.asyncroutingdemo.file.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.sqs.Sqs2Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class EIPRoutePatternsSNS extends RouteBuilder{
	
	@Autowired
	DeciderBean2 deciderBean;

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

      .to("aws2-sns://myContactForm?accessKey=RAW(AKIAUFWMQGICW7MUHCER)&secretKey=RAW(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)&region=ap-south-1")
      .log("${body}");
		
	}

}

@Component
class DeciderBean2{
	Logger logger=LoggerFactory.getLogger(DeciderBean.class);
	public boolean isRequestContain(String body) {
		logger.info("Body {}",body);
		
		return true;
	}
}










