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

import java.util.ArrayList;
import java.util.List;

import io.kubernetes.client.openapi.models.V1ConfigMapKeySelector;
import io.kubernetes.client.openapi.models.V1SecretKeySelector;

public class HDFSArtifact {
	private List<String> addresses = new ArrayList<>();

	private Boolean force;

	private String hdfsUser;

	private V1SecretKeySelector krbCCacheSecret = null;

	private V1ConfigMapKeySelector krbConfigConfigMap = null;

	private V1SecretKeySelector krbKeytabSecret = null;

	private String krbRealm;

	private String krbServicePrincipalName;

	private String krbUsername;

	private String path;
}
