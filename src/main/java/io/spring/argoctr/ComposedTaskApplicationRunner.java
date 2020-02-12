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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.spring.argoctr.domain.Workflow;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.StringUtils;

public class ComposedTaskApplicationRunner implements ApplicationRunner {

	private ComposedTaskProperties properties;

	private ParsedWorkFlows parsedWorkFlows;

	public ComposedTaskApplicationRunner(ComposedTaskProperties properties, ParsedWorkFlows parsedWorkFlows) {
		this.properties = properties;
		this.parsedWorkFlows = parsedWorkFlows;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(!StringUtils.hasText(this.properties.getGraph() )|| !StringUtils.hasText(this.properties.getWorkFlowName())) {
			System.out.println("The format for using this application is as follows: \n \n java -jar argoctr-1.0.0.BUILD-SNAPSHOT.jar --workFlowName=<name of workflow> --graph=<expression for composed task>\n\n" );
			return;
		}
		launchWorkFlow(this.properties.getWorkFlowName(), this.properties.getGraph());
		System.exit(0);
	}


	private void launchWorkFlow(String workFlowName, String graph) throws Exception{
		Workflow workflow = this.parsedWorkFlows.parseWorkFlow(workFlowName, graph);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		System.out.println(mapper.writeValueAsString(workflow));
		submitRequest(workflow);
	}

	public Object submitRequest(Workflow workflow) throws Exception{
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
		LinkedTreeMap<String, LinkedTreeMap> map = (LinkedTreeMap<String,LinkedTreeMap>)result;
		LinkedTreeMap<String,LinkedTreeMap> metaDataMap = map.get("metadata");
		System.out.println(String.format("\n\n\"%s\" workflow was launched by Argo.\n", metaDataMap.get("name")));
		return result;
	}
}
