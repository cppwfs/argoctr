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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class ComposedTaskProperties {

	private String graph;

	private String workFlowName;

	private boolean deployWorkflow = true;

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public String getWorkFlowName() {
		return workFlowName;
	}

	public void setWorkFlowName(String workFlowName) {
		this.workFlowName = workFlowName;
	}

	public boolean isDeployWorkflow() {
		return deployWorkflow;
	}

	public void setDeployWorkflow(boolean deployWorkflow) {
		this.deployWorkflow = deployWorkflow;
	}
}
