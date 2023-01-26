package com.redhat.demo.asyncroutingdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan("com.redhat.demo.asyncroutingdemo")
public class AsyncRoutingDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncRoutingDemoApplication.class, args);
	}

	// @Bean
	// public ServletRegistrationBean camelServletRegistrationBean() {
	//   ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
	//   registration.setName("CamelServlet2");
	//   return registration;
	// }
}
