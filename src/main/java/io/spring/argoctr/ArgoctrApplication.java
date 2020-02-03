package io.spring.argoctr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ManagedFieldsEntry;
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.Config;
import io.spring.argoctr.domain.Arguments;
import io.spring.argoctr.domain.Artifact;
import io.spring.argoctr.domain.DAGTask;
import io.spring.argoctr.domain.DAGTemplate;
import io.spring.argoctr.domain.Outputs;
import io.spring.argoctr.domain.Parameter;
import io.spring.argoctr.domain.Template;
import io.spring.argoctr.domain.ValueFrom;
import io.spring.argoctr.domain.Workflow;
import io.spring.argoctr.domain.WorkflowSpecification;
import javax.management.MXBean;
import org.checkerframework.checker.units.qual.A;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArgoctrApplication {

	@Autowired
	private FixedWorkFlows fixedWorkFlows;

	@Autowired
	private ParsedWorkFlows parsedWorkFlows;

	public static void main(String[] args) {
		SpringApplication.run(ArgoctrApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(FixedWorkFlows fixedWorkFlows) {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				launchParsedFlows();
//				launchFixedWorkFlow();
				System.exit(0);
			}
		};
	}

	private void launchParsedFlows() throws Exception{
		//Workflow workflow = parsedWorkFlows.parseWorkFlow("sequenceargoflow", "A: timestamp --foo=bar && B: timestamp --baz=boo --bar=graph && C: timestamp");
		//Workflow workflow = parsedWorkFlows.parseWorkFlow("sequenceargoflow", "A: timestamp --foo=bar --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root && B: timestamp --baz=boo --bar=graph --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root && C: timestamp --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root");
		Workflow workflow = parsedWorkFlows.parseWorkFlow("sequenceargoflownolabel", "timestamp --foo=bar --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root && B: timestamp --baz=boo --bar=graph --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root && C: timestamp --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root");
		//Workflow workflow = parsedWorkFlows.parseWorkFlow("directedgraph", "A: argo-task 'FAILED' -> B: argo-task 'COMPLETED' -> C: timestamp");
		//Workflow workflow = parsedWorkFlows.parseWorkFlow("singletask", "timestamp --foo=bar --spring.datasource.driverClassName=org.mariadb.jdbc.Driver --spring.datasource.password=yourpassword --spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql --spring.datasource.username=root ");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		System.out.println(mapper.writeValueAsString(workflow));

	}

	private void launchFixedWorkFlow() throws Exception{
		//Workflow workflow = submitTask();
		Workflow workflow = this.fixedWorkFlows.submitSequence();
		//Workflow workflow = fixedWorkFlows.submitSplit();
		//Workflow workflow = fixedWorkFlows.submitFailedSequence();//submitTask();
//		Workflow workflow = this.fixedWorkFlows.submitConditional();
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		System.out.println(mapper.writeValueAsString(workflow));
		System.out.println(this.fixedWorkFlows.submitRequest(workflow));
	}

}
