package com.kinancity.server.web.dto;

import com.kinancity.server.entity.HttpProxyConfig;

import lombok.Data;

@Data
public class ProxyConfigModel {

	private long id;

	// Should it be used
	private boolean active;

	// HttpProxy or NoProxy
	private String type;

	// Proxy URI
	private String uri;

	// Serialized Policy
	private String policy;

	public ProxyConfigModel(HttpProxyConfig config) {
		this.setId(config.getId());
		this.setActive(config.isActive());
		this.setType(config.getType());
		this.setUri(config.getUri());
		this.setPolicy(config.getPolicy());
	}

}
