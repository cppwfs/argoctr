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

import java.util.List;

public class DAGTask {
	private Arguments arguments = null;

	private ContinueOn continueOn = null;

	private List<String> dependencies = null;

	private String name;

	private String template;

	private String when;

	private List<String> withItems = null;

	private String withParam;

	private Sequence withSequence = null;

	public Arguments getArguments() {
		return arguments;
	}

	public void setArguments(Arguments arguments) {
		this.arguments = arguments;
	}

	public ContinueOn getContinueOn() {
		return continueOn;
	}

	public void setContinueOn(ContinueOn continueOn) {
		this.continueOn = continueOn;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public List<String> getWithItems() {
		return withItems;
	}

	public void setWithItems(List<String> withItems) {
		this.withItems = withItems;
	}

	public String getWithParam() {
		return withParam;
	}

	public void setWithParam(String withParam) {
		this.withParam = withParam;
	}

	public Sequence getWithSequence() {
		return withSequence;
	}

	public void setWithSequence(Sequence withSequence) {
		this.withSequence = withSequence;
	}
}
