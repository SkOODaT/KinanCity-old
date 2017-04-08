package com.kinancity.server.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AddProxyModel", description = "Model for adding a new proxy")
public class AddProxyModel {

	@ApiModelProperty(value = "Proxy formatted as URL", example= "http://login:pass@127.0.0.12:3128")
	private String proxyUrl;

	@ApiModelProperty(value = "Policy", allowableValues = "nintendo, unlimited")
	private String policy;

}
