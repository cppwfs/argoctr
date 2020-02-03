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

package io.spring.argoctr.configuration;

import io.spring.argoctr.FakeAppRegistryService;
import io.spring.argoctr.FixedWorkFlows;
import io.spring.argoctr.ParsedWorkFlows;

import org.springframework.cloud.dataflow.registry.service.AppRegistryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfiguration {


	@Bean
	public AppRegistryService fakeAppRegistryService() throws Exception{
		return new FakeAppRegistryService();
	}

	@Bean
	public FixedWorkFlows fixedWorkFlows() {
		return new FixedWorkFlows();
	}

	@Bean
	public ParsedWorkFlows parsedWorkFlows(AppRegistryService appRegistryService) {
		return new ParsedWorkFlows(appRegistryService);
	}
}
