package com.kinancity.server.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UpdateProxyModel", description = "Model for updating/deleting a proxy")
public class UpdateProxyModel {

	public static final String ACTIVATE = "activate";
	public static final String DEACTIVATE = "deactivate";
	public static final String DELETE = "delete";
	
	@ApiModelProperty(value = "id of the proxy in db")
	private long id;
	
	@ApiModelProperty(value = "action", allowableValues = "activate, deactivate, delete")
	private String action;

	
}
