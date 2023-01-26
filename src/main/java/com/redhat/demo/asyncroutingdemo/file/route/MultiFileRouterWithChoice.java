package com.redhat.demo.asyncroutingdemo.file.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.sqs.Sqs2Constants;
import org.apache.camel.component.sql.SqlComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.redhat.demo.asyncroutingdemo.model.Employee;

@Component
public class MultiFileRouterWithChoice extends RouteBuilder{
	
	@Autowired
	ProducerTemplate producerTemplate;
	
	@Autowired
	DeciderBean deciderBean;
	@Autowired
	DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	public SqlComponent sql(DataSource dataSource) {
		SqlComponent sql = new SqlComponent();
		sql.setDataSource(dataSource);
		return sql;
	}

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
			.log("IMD Exist IN database")
		.otherwise()
		 	.log("File not xml")
		 
		 
		 .end()
		 .log("${body}")
		 .to("file:files/output")
		 .to("direct:multicast-parraller-process");
		
		
		from("direct:to_aws-sqs") 
      .to("aws2-sqs://camelqueue?accessKey=RAW(AKIAUFWMQGICW7MUHCER)&secretKey=RAW(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)&region=ap-south-1")
      .log("${body}");
		
		
		from("direct:multicast-parraller-process").multicast().parallelProcessing()
	      .to("direct:to_aws-sqs","direct:insert")
	      .log("${body}");
		
		
		
		from("direct:select").to("sql:select * from policy").process(new Processor() {
			public void process(Exchange xchg) throws Exception {
				ArrayList<Map<String, String>> dataList = (ArrayList<Map<String, String>>) xchg.getIn().getBody();
				List<String> listImdCode = new ArrayList<String>();
				System.out.println(dataList);
				for (Map<String, String> data : dataList) {
					System.out.println("Received idm Code   "+ data.get("imdcode"));
					listImdCode.add(data.get("imdcode"));
				}
				xchg.getIn().setBody(listImdCode);
			}
		}).log("${body}");
		
		
		from("direct:insert").log("Processing message: ${body}").setHeader("message", body()).process(new Processor() {
			public void process(Exchange xchg) throws Exception {
				String body = xchg.getIn().getBody(String.class);
				Map<String, Object> answer = new HashMap<String, Object>();
				answer.put("imdcode", body);				 
				xchg.getIn().setBody(answer);
			}
		}).to("sql:INSERT INTO policy(imdcode) VALUES (:#imdcode)");
		
	}

}

@Component
class DeciderBean{
	
	@Autowired
	ProducerTemplate producerTemplate;
	
	Logger logger=LoggerFactory.getLogger(DeciderBean.class);
	public boolean isImdCodeExisInDB(String body) {
		
		List<String> listImdCode =	producerTemplate.requestBody("direct:select", null, List.class);
		logger.info("Body {}",body);
		if(listImdCode.size()==0)
			return false;
		
		return true;
	}
}










