package com.redhat.demo.asyncroutingdemo.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.redhat.demo.asyncroutingdemo.model.Employee;

 
//@Component
public class DataBaseRoute extends RouteBuilder{
	
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
		from("file:files/input").to("direct:select")
		.log("${body}");
		from("direct:select").to("sql:select * from policy").process(new Processor() {
			public void process(Exchange xchg) throws Exception {
				ArrayList<Map<String, String>> dataList = (ArrayList<Map<String, String>>) xchg.getIn().getBody();
				List<Employee> employees = new ArrayList<Employee>();
				System.out.println(dataList);
				for (Map<String, String> data : dataList) {
					Employee employee = new Employee();
					employee.setEmpId(data.get("imdcode"));
					employee.setEmpName(data.get("imdcode"));
					employees.add(employee);
				}
				xchg.getIn().setBody(employees);
			}
		}).log("${body}");
		
	}
	

}
