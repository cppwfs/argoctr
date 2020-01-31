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

import io.kubernetes.client.openapi.models.V1Container;

public class Template implements Serializable {

	private String name;
	private V1Container container;
	private DAGTemplate dag;

	private Outputs outputs;

	public Outputs getOutputs() {
		return outputs;
	}

	public void setOutputs(Outputs outputs) {
		this.outputs = outputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public V1Container getContainer() {
		return container;
	}

	public void setContainer(V1Container container) {
		this.container = container;
	}

	public DAGTemplate getDag() {
		return dag;
	}

	public void setDag(DAGTemplate dag) {
		this.dag = dag;
	}
}
