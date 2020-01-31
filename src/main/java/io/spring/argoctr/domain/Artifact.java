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

public class Artifact {
	private ArchiveStrategy archive = null;

	private Boolean archiveLogs;

	private ArtifactoryArtifact artifactory = null;

	private String from;

	private GitArtifact git = null;

	private String globalName;

	private HDFSArtifact hdfs = null;

	private HTTPArtifact http = null;

	private Integer mode;

	private String name;

	private Boolean optional;

	private String path;

	private RawArtifact raw = null;

	private S3Artifact s3 = null;

	public ArchiveStrategy getArchive() {
		return archive;
	}

	public void setArchive(ArchiveStrategy archive) {
		this.archive = archive;
	}

	public Boolean getArchiveLogs() {
		return archiveLogs;
	}

	public void setArchiveLogs(Boolean archiveLogs) {
		this.archiveLogs = archiveLogs;
	}

	public ArtifactoryArtifact getArtifactory() {
		return artifactory;
	}

	public void setArtifactory(ArtifactoryArtifact artifactory) {
		this.artifactory = artifactory;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public GitArtifact getGit() {
		return git;
	}

	public void setGit(GitArtifact git) {
		this.git = git;
	}

	public String getGlobalName() {
		return globalName;
	}

	public void setGlobalName(String globalName) {
		this.globalName = globalName;
	}

	public HDFSArtifact getHdfs() {
		return hdfs;
	}

	public void setHdfs(HDFSArtifact hdfs) {
		this.hdfs = hdfs;
	}

	public HTTPArtifact getHttp() {
		return http;
	}

	public void setHttp(HTTPArtifact http) {
		this.http = http;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public RawArtifact getRaw() {
		return raw;
	}

	public void setRaw(RawArtifact raw) {
		this.raw = raw;
	}

	public S3Artifact getS3() {
		return s3;
	}

	public void setS3(S3Artifact s3) {
		this.s3 = s3;
	}
}
