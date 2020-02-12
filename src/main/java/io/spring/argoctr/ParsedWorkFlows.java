/*
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.spring.argoctr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.kubernetes.client.openapi.models.V1Container;
import io.spring.argoctr.domain.DAGTask;
import io.spring.argoctr.domain.DAGTemplate;
import io.spring.argoctr.domain.Outputs;
import io.spring.argoctr.domain.Parameter;
import io.spring.argoctr.domain.Template;
import io.spring.argoctr.domain.ValueFrom;
import io.spring.argoctr.domain.Workflow;

import org.springframework.cloud.dataflow.core.AppRegistration;
import org.springframework.cloud.dataflow.core.ApplicationType;
import org.springframework.cloud.dataflow.core.dsl.ArgumentNode;
import org.springframework.cloud.dataflow.core.dsl.FlowNode;
import org.springframework.cloud.dataflow.core.dsl.LabelledTaskNode;
import org.springframework.cloud.dataflow.core.dsl.TaskAppNode;
import org.springframework.cloud.dataflow.core.dsl.TaskParser;
import org.springframework.cloud.dataflow.core.dsl.TransitionNode;
import org.springframework.cloud.dataflow.registry.service.AppRegistryService;

public class ParsedWorkFlows {

	private static final String WILD_CARD = "*";

	private AppRegistryService appRegistryService;

	private Deque<DAGTemplate> jobDeque = new LinkedList<>();

	private Deque<LabelledTaskNode> visitorDeque;

	private Deque<DAGTask> executionDeque = new LinkedList<>();

	private Deque<Template> containerQueue = new LinkedList<>();

	private Map<String, Integer> containerTemplateNameIndex = new HashMap<>();


	public ParsedWorkFlows(AppRegistryService appRegistryService) {
		this.appRegistryService = appRegistryService;
	}

	public Workflow parseWorkFlow(String taskDefinitionName, String dsl) {
		Workflow workflow = new Workflow();
		workflow.getMetadata().generateName(taskDefinitionName + "-");
		workflow.getSpec().setEntrypoint("composed-task");
		workflow.getSpec().setTemplates(getTemplates(dsl));
		return workflow;
	}

	public List<Template> getTemplates(String dsl) {
		List<Template> templates = new ArrayList<>();
		ComposedRunnerVisitor composedRunnerVisitor = new ComposedRunnerVisitor();

		TaskParser taskParser = new TaskParser("composed-task-runner",
				dsl, true, true);
		taskParser.parse().accept(composedRunnerVisitor);

		this.visitorDeque = composedRunnerVisitor.getFlow();
		for(LabelledTaskNode node : this.visitorDeque) {
			System.out.println(node);
		}

		this.executionDeque.stream().forEach(node -> System.out.println(node));

		templates.add(dagBuilder(dsl));

		while (!this.containerQueue.isEmpty()) {
			templates.add(this.containerQueue.pop());
		}

		return templates;
	}

	public Template dagBuilder(String dsl) {
		ComposedRunnerVisitor composedRunnerVisitor = new ComposedRunnerVisitor();

		TaskParser taskParser = new TaskParser("composed-task-runner",
				dsl, true, true);
		taskParser.parse().accept(composedRunnerVisitor);

		this.visitorDeque = composedRunnerVisitor.getFlow();
		Template template = new Template();
		DAGTemplate dagTemplate = createFlow();
		template.setName("composed-task");
		template.setDag(dagTemplate);
		return template;
	}

	private DAGTemplate createFlow() {
		LabelledTaskNode previousTaskNode = null;
		DAGTemplate dagTemplate = new DAGTemplate();
		while (!this.visitorDeque.isEmpty()) {
			if (this.visitorDeque.peek().isFlow()) {
				break;
			}
			if (this.visitorDeque.peek().isTaskApp()) {
				TaskAppNode taskAppNode = (TaskAppNode) this.visitorDeque.pop();
				if (taskAppNode.hasTransitions()) {
					handleTransition(taskAppNode);
				}
				else {
					handleTaskAppFlow(taskAppNode);
				}
			}
		}
		while (!this.executionDeque.isEmpty()) {
			dagTemplate.getTasks().add(this.executionDeque.pop());
		}
		return dagTemplate;
	}

	private Template getContainerTemplateForNode(TaskAppNode taskAppNode, String containerName) {
		Template template = new Template();
		template.setName(containerName);
		V1Container container = new V1Container();
		AppRegistration appRegistration = this.appRegistryService.find(taskAppNode.getName(), ApplicationType.task);
		String uri = appRegistration.getUri().toString();
		uri = uri.substring(uri.indexOf(":") + 1);
		container.setImage(uri);

		List<String> argsList = null;
		if (taskAppNode.getArguments() != null) {
			argsList = new ArrayList(taskAppNode.getArguments().length);

			for (ArgumentNode argumentNode : taskAppNode.getArguments()) {
				argsList.add(argumentNode.toString());
			}
		}
		container.setArgs(argsList);
		template.setContainer(container);

		return template;
	}

	private String getIdentifier(LabelledTaskNode labelledTaskNode) {
		if (labelledTaskNode.getLabel() == null && labelledTaskNode instanceof TaskAppNode) {
			return ((TaskAppNode) labelledTaskNode).getName();
		}
		return (labelledTaskNode.getLabel() == null) ? null : labelledTaskNode.getLabel().stringValue();
	}

	private void handleTaskAppFlow(TaskAppNode taskAppNode) {
		DAGTask dagTask = new DAGTask();
		LabelledTaskNode dependencyNode = this.visitorDeque.peek();
		List<String> dependencies = null;
		if (!(dependencyNode.getLabel() == null && dependencyNode instanceof FlowNode) && !getIdentifier(dependencyNode).equals(getIdentifier(taskAppNode))) {
			dependencies = new ArrayList<>(1);
			dependencies.add(getIdentifier(dependencyNode));
		}
		dagTask.setDependencies(dependencies);
		dagTask.setName(getIdentifier(taskAppNode));
		dagTask.setTemplate(getContainerNameWithIndex(taskAppNode));
		this.executionDeque.push(dagTask);
		this.containerQueue.push(getContainerTemplateForNode(taskAppNode, dagTask.getTemplate()));
	}

	private void handleTransition(TaskAppNode taskAppNode) {
		String targetParamName = "argo-task-param";
		boolean wildCardPresent = false;
		handleTaskAppFlow(taskAppNode);
		Outputs containerTemplateOutputs = new Outputs();
		Parameter parameter = new Parameter();
		parameter.setName(targetParamName);
		containerTemplateOutputs.setParameters(Collections.singletonList(parameter));
		ValueFrom valueFrom = new ValueFrom();
		valueFrom.setPath("/tmp/hello_world.txt");
		parameter.setValueFrom(valueFrom);
		this.containerQueue.peek().setOutputs(containerTemplateOutputs);

		for (TransitionNode transitionNode : taskAppNode.getTransitions()) {

			wildCardPresent = transitionNode.getStatusToCheck().equals(WILD_CARD);
			DAGTask dagTask = new DAGTask();
			List<String> dependencies = null;
			LabelledTaskNode dependencyNode = taskAppNode;
			if(!wildCardPresent) {
				dependencies = new ArrayList<>(1);
				dependencies.add(getIdentifier(dependencyNode));
				dagTask.setDependencies(dependencies);
			}
			dagTask.setName(getIdentifier(transitionNode.getTargetApp()));
			String templateName = getContainerNameWithIndex(transitionNode.getTargetApp());
			dagTask.setTemplate(templateName);
			if(!wildCardPresent) {
				dagTask.setWhen("{{tasks." + getIdentifier(taskAppNode) + ".outputs.parameters." +
						targetParamName + "}} == " + transitionNode.getStatusToCheck());
			}
			this.executionDeque.add(dagTask);

			//Setup Container Template for transition
			Template containerTemplate = getContainerTemplateForNode(transitionNode.getTargetApp(), dagTask.getTemplate());
			containerTemplate.setName(templateName);
			this.containerQueue.push(containerTemplate);

		}
	}

	private String getContainerNameWithIndex(TaskAppNode taskAppNode) {
		String containerTaskName;
		if (this.containerTemplateNameIndex.containsKey(taskAppNode.getName())) {
			Integer index = this.containerTemplateNameIndex.get(taskAppNode.getName());
			containerTaskName = taskAppNode.getName() + index;
			index++;
			this.containerTemplateNameIndex.put(taskAppNode.getName(), index);
		}
		else {
			this.containerTemplateNameIndex.put(taskAppNode.getName(), 0);
			containerTaskName = taskAppNode.getName();
		}
		return containerTaskName;
	}
}
