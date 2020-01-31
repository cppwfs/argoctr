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

	public static void main(String[] args) {
		SpringApplication.run(ArgoctrApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				//Workflow workflow = submitTask();
				//Workflow workflow = submitSequence();
				//Workflow workflow = submitSplit();
				//Workflow workflow = submitFailedSequence();//submitTask();
				Workflow workflow = submitConditional();
				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
				mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				System.out.println(mapper.writeValueAsString(workflow));
				System.out.println(submitRequest(workflow));
				System.exit(0);
			}
		};
	}

	private Workflow submitHelloWorldDefault() {
		String entryPoint = "whalesay";
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName("hello-world-");
		workflow.getSpec().setEntrypoint(entryPoint);
		Template template = new Template();
		template.setName("whalesay");
		V1Container container = new V1Container();
		container.setImage("docker/whalesay:latest");
		container.setCommand(Arrays.asList("cowsay"));
		container.setArgs(Arrays.asList("hello world"));
		template.setContainer(container);

		//Add Template to spec
		workflow.getSpec().getTemplates().add(template);
		return workflow;
	}

	private Workflow submitTask() {
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName("task-");
		workflow.getSpec().setEntrypoint("timestamp-task");

		//Add Template to spec
		workflow.getSpec().getTemplates().add(getTimestampTemplate());
		return workflow;
	}

	private Workflow submitSequence() {
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName("task-");
		workflow.getSpec().setEntrypoint("task-sequence");
		Template timestampTemplate = getTimestampTemplate();
		buildSequence(workflow.getSpec());
		workflow.getSpec().getTemplates().add(timestampTemplate);
		return workflow;
	}

	private void buildSequence(WorkflowSpecification spec) {
		Template template = new Template();
		DAGTemplate dagTemplate =  new DAGTemplate();
		template.setName("task-sequence");
		template.setDag(dagTemplate);
		DAGTask dagTask = new DAGTask();
		dagTask.setName("A");
		dagTask.setTemplate("timestamp-task");
		dagTemplate.getTasks().add(dagTask);

		dagTask = new DAGTask();
		dagTask.setName("B");
		dagTask.setTemplate("timestamp-task");
		dagTask.setDependencies(Collections.singletonList("A"));
		dagTemplate.getTasks().add(dagTask);

		//Add Template with DAG to spec
		spec.getTemplates().add(template);
	}

	private Workflow submitSplit() {
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName("task-split-wf-");
		workflow.getSpec().setEntrypoint("task-split");
		Template timestampTemplate = getArgoTaskTemplate(null);
		buildSplit(workflow.getSpec());
		workflow.getSpec().getTemplates().add(timestampTemplate);
		return workflow;
	}

	private void buildSplit(WorkflowSpecification spec) {
		Template template = new Template();
		DAGTemplate dagTemplate =  new DAGTemplate();
		template.setName("task-split");
		template.setDag(dagTemplate);

		// Create DAG First task step
		DAGTask dagTask = new DAGTask();
		dagTask.setName("A");
		dagTask.setTemplate("argo-task");
		dagTemplate.getTasks().add(dagTask);

		// Create DAG Second task step to run simultaneous with third
		dagTask = new DAGTask();
		dagTask.setName("B");
		dagTask.setTemplate("argo-task");
		dagTask.setDependencies(Collections.singletonList("A"));
		dagTemplate.getTasks().add(dagTask);

		// Create DAG Third task step to run simultaneous with second
		dagTask = new DAGTask();
		dagTask.setName("C");
		dagTask.setTemplate("argo-task");
		dagTask.setDependencies(Collections.singletonList("A"));
		dagTemplate.getTasks().add(dagTask);

		//Add Template with DAG to spec
		spec.getTemplates().add(template);
	}

	private Workflow submitConditional() {
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName("task-conditional-wf-");
		workflow.getSpec().setEntrypoint("task-conditional");
		Template timestampTemplate = getArgoTaskTemplate(null);
		buildConditional(workflow.getSpec());
		workflow.getSpec().getTemplates().add(timestampTemplate);
		return workflow;
	}

	private void buildConditional(WorkflowSpecification spec) {
		Template template = createDAGTemplate("task-conditional");
		DAGTemplate dagTemplate =  template.getDag();

		// Create DAG First task step
		DAGTask dagTask = new DAGTask();
		dagTask.setName("A");
		dagTask.setTemplate("argo-task");
		dagTemplate.getTasks().add(dagTask);


		// Create DAG Second task step that will fire if output file contains heads
		dagTask = new DAGTask();
		dagTask.setName("B");
		dagTask.setTemplate("argo-task");
		dagTask.setDependencies(Collections.singletonList("A"));
		dagTask.setWhen("{{tasks.A.outputs.parameters.argo-task-param}} == heads");
		dagTemplate.getTasks().add(dagTask);

		// Create DAG Second task step that will fire if output file contains tails
		dagTask = new DAGTask();
		dagTask.setName("C");
		dagTask.setTemplate("argo-task");
		dagTask.setDependencies(Collections.singletonList("A"));
		dagTask.setWhen("{{tasks.A.outputs.parameters.argo-task-param}} == tails");
		dagTemplate.getTasks().add(dagTask);

		//Add Template with DAG to spec
		spec.getTemplates().add(template);
	}

	private Workflow submitFailedSequence() {
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName("task-fail-");
		workflow.getSpec().setEntrypoint("task-sequence");
		Template timestampTemplate = getTimestampTemplate();
		buildFailedSequence(workflow.getSpec());
		workflow.getSpec().getTemplates().add(timestampTemplate);
		List<String> additionalArgs = new ArrayList<>(1);
		additionalArgs.add("--abEnd=true");
		workflow.getSpec().getTemplates().add(getArgoTaskTemplate(additionalArgs));
		return workflow;
	}

	private void buildFailedSequence(WorkflowSpecification spec) {
		Template template = createDAGTemplate("task-sequence");
		DAGTemplate dagTemplate = template.getDag();

		// Create DAG First task step
		DAGTask dagTask = new DAGTask();
		dagTask.setName("A");
		dagTask.setTemplate("timestamp-task");
		dagTemplate.getTasks().add(dagTask);

		// Create DAG Second (fail) task step
		dagTask = new DAGTask();
		dagTask.setName("B");
		dagTask.setTemplate("argo-task");
		dagTask.setDependencies(Collections.singletonList("A"));
		dagTemplate.getTasks().add(dagTask);

		// Create DAG Third task step
		dagTask = new DAGTask();
		dagTask.setName("C");
		dagTask.setTemplate("timestamp-task");
		dagTask.setDependencies(Collections.singletonList("B"));
		dagTemplate.getTasks().add(dagTask);

		//Add Template with DAG to spec
		spec.getTemplates().add(template);
	}

	private Template getArgoTaskTemplate(List<String> additionalArgs) {
		Template template = new Template();
		template.setName("argo-task");
		V1Container container = new V1Container();
		container.setImage("cppwfs/argotask:1.0.0.BUILD-SNAPSHOT");
//		container.setCommand(Arrays.asList("cowsay"));
		List argsList = new ArrayList(4);
		argsList.add("--spring.datasource.driverClassName=org.mariadb.jdbc.Driver");
		argsList.add("--spring.datasource.password=yourpassword");
		argsList.add("--spring.datasource.url=jdbc:mysql://10.111.140.239:3306/mysql");
		argsList.add("--spring.datasource.username=root");
		argsList.add("--message=tails");
		if(additionalArgs != null) {
			argsList.addAll(additionalArgs);
		}
		container.setArgs(argsList);
		template.setContainer(container);
		Outputs outputs = new Outputs();
		Parameter parameter = new Parameter();
		parameter.setName("argo-task-param");
		ValueFrom valueFrom = new ValueFrom();
		valueFrom.setPath("/tmp/hello_world.txt");
		parameter.setValueFrom(valueFrom);
		outputs.setParameters(Collections.singletonList(parameter));
		template.setOutputs(outputs);

		return template;
	}

	private Template getTimestampTemplate() {
		Template template = new Template();
		template.setName("timestamp-task");
		V1Container container = new V1Container();
		container.setImage("springcloudtask/timestamp-task:2.1.0.RELEASE");
//		container.setCommand(Arrays.asList("cowsay"));
		List argsList = new ArrayList(4);
		argsList.add("--spring.datasource.driverClassName=org.mariadb.jdbc.Driver");
		argsList.add("--spring.datasource.password=yourpassword");
		argsList.add("--spring.datasource.url=jdbc:mysql://stuff/mysql");
		argsList.add("--spring.datasource.username=root");
		container.setArgs(argsList);
		template.setContainer(container);

		return template;
	}

	private Object submitRequest(Workflow workflow) throws Exception{
		ApiClient client = Config.defaultClient();
		client.setDebugging(true);
		Configuration.setDefaultApiClient(client);

		CustomObjectsApi apiInstance = new CustomObjectsApi();
		Object result = null;
		try {
			result = apiInstance.createNamespacedCustomObject("argoproj.io", "v1alpha1", "default", "workflows", workflow, "true");
		}
		catch (ApiException e) {
			e.printStackTrace();
		}
		return result;
	}

	private Template createDAGTemplate(String templateName) {
		Template template = new Template();
		DAGTemplate dagTemplate =  new DAGTemplate();
		template.setName(templateName);
		template.setDag(dagTemplate);
		return template;
	}
}
