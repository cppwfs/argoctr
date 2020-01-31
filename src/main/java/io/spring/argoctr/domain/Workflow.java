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

package io.spring.argoctr.domain;

import java.io.Serializable;

import io.kubernetes.client.openapi.models.V1ObjectMeta;



public class Workflow implements Serializable {
	private String apiVersion = "argoproj.io/v1alpha1";
	private String kind = "Workflow";
	private V1ObjectMeta metadata;
	private WorkflowSpecification spec;
	private WorkflowStatus workflowStatus;

	public Workflow() {
		this.spec = new WorkflowSpecification();
		metadata = new V1ObjectMeta();
//		this.workflowStatus = new WorkflowStatus();
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public V1ObjectMeta getMetadata() {
		return metadata;
	}

	public void setMetadata(V1ObjectMeta metadata) {
		this.metadata = metadata;
	}

	public WorkflowSpecification getSpec() {
		return spec;
	}

	public void setSpec(WorkflowSpecification spec) {
		this.spec = spec;
	}

	public WorkflowStatus getWorkflowStatus() {
		return workflowStatus;
	}

	public void setWorkflowStatus(WorkflowStatus workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

}
