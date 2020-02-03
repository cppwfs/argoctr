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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.dataflow.core.AppRegistration;
import org.springframework.cloud.dataflow.core.ApplicationType;
import org.springframework.cloud.dataflow.registry.service.AppRegistryService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class FakeAppRegistryService implements AppRegistryService {
	private Map<String, AppRegistration> registrations = new HashMap<>();
	public FakeAppRegistryService() throws Exception{
		AppRegistration appRegistration = new AppRegistration();
		appRegistration.setName("timestamp");
		appRegistration.setUri(new URI("docker:springcloudtask/timestamp-task:2.1.0.RELEASE"));
		registrations.put("timestamp", appRegistration);
		appRegistration = new AppRegistration();
		appRegistration.setName("argo-task");
		appRegistration.setUri(new URI("docker:cppwfs/argotask"));
		registrations.put("argo-task", appRegistration);
	}

	@Override
	public AppRegistration getDefaultApp(String name, ApplicationType type) {
		return null;
	}

	@Override
	public void validate(AppRegistration registration, String uri, String version) {

	}

	@Override
	public void setDefaultApp(String name, ApplicationType type, String version) {

	}

	@Override
	public AppRegistration save(String name, ApplicationType type, String version, URI uri, URI metadataUri) {
		return null;
	}

	@Override
	public void delete(String name, ApplicationType type, String version) {

	}

	@Override
	public void deleteAll(Iterable<AppRegistration> appRegistrations) {

	}

	@Override
	public boolean appExist(String name, ApplicationType type, String version) {
		return false;
	}

	@Override
	public Page<AppRegistration> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public Page<AppRegistration> findAllByTypeAndNameIsLike(ApplicationType type, String name, Pageable pageable) {
		return null;
	}

	@Override
	public boolean appExist(String name, ApplicationType type) {
		return false;
	}

	@Override
	public List<AppRegistration> findAll() {
		return null;
	}

	@Override
	public AppRegistration find(String name, ApplicationType type) {
		return this.registrations.get(name);
	}

	@Override
	public List<AppRegistration> importAll(boolean overwrite, Resource... resources) {
		return null;
	}

	@Override
	public Resource getAppResource(AppRegistration appRegistration) {
		return null;
	}

	@Override
	public Resource getAppMetadataResource(AppRegistration appRegistration) {
		return null;
	}

	@Override
	public AppRegistration save(AppRegistration app) {
		return null;
	}

	@Override
	public String getResourceVersion(Resource resource) {
		return null;
	}

	@Override
	public String getResourceWithoutVersion(Resource resource) {
		return null;
	}

	@Override
	public String getResourceVersion(String uriString) {
		return null;
	}
}
