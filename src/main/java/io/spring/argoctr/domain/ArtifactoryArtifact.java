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

import io.kubernetes.client.openapi.models.V1SecretKeySelector;

public class ArtifactoryArtifact {

	private V1SecretKeySelector passwordSecret = null;

	private String url;

	private V1SecretKeySelector usernameSecret = null;

	public V1SecretKeySelector getPasswordSecret() {
		return passwordSecret;
	}

	public void setPasswordSecret(V1SecretKeySelector passwordSecret) {
		this.passwordSecret = passwordSecret;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public V1SecretKeySelector getUsernameSecret() {
		return usernameSecret;
	}

	public void setUsernameSecret(V1SecretKeySelector usernameSecret) {
		this.usernameSecret = usernameSecret;
	}
}
