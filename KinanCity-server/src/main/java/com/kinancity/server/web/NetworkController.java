package com.kinancity.server.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kinancity.core.proxy.HttpProxyProvider;
import com.kinancity.core.proxy.ProxyInfo;
import com.kinancity.core.proxy.impl.HttpProxy;
import com.kinancity.core.proxy.policies.NintendoTimeLimitPolicy;
import com.kinancity.core.proxy.policies.ProxyPolicy;
import com.kinancity.core.proxy.policies.UnlimitedUsePolicy;
import com.kinancity.server.entity.HttpProxyConfig;
import com.kinancity.server.entity.PolicyConfig;
import com.kinancity.server.entity.factory.HttpProxyConfigFactory;
import com.kinancity.server.entity.mapper.PolicyConfigMapper;
import com.kinancity.server.entity.repository.HttpProxyConfigRepository;
import com.kinancity.server.entity.repository.PolicyConfigRepository;
import com.kinancity.server.web.dto.AddProxyModel;
import com.kinancity.server.web.dto.ProxyConfigModel;
import com.kinancity.server.web.dto.UpdateProxyModel;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

	@Autowired
	private HttpProxyConfigRepository httpProxyConfigRepository;

	@Autowired
	private PolicyConfigRepository policyConfigRepository;

	@ApiOperation(value = "Index of saved proxies")
	@RequestMapping(value = "/proxy", method = RequestMethod.GET)
	public @ResponseBody List<ProxyConfigModel> proxyIndex() {
		return httpProxyConfigRepository.findAll().stream().map(ProxyConfigModel::new).collect(Collectors.toList());
	}

	@ApiOperation(value = "Add a new Proxy")
	@RequestMapping(value = "/proxy", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ProxyConfigModel> addProxy(@RequestBody AddProxyModel addProxy) {

		// Policy
		ProxyPolicy proxyPolicy;
		switch (addProxy.getPolicy()) {
		case "nintendo":
			proxyPolicy = new NintendoTimeLimitPolicy();
			break;
		case "unlimited":
			proxyPolicy = new UnlimitedUsePolicy();
			break;
		default:
			return new ResponseEntity<ProxyConfigModel>(HttpStatus.BAD_REQUEST);
		}

		// Provider
		HttpProxyProvider provider = HttpProxy.fromURI(addProxy.getProxyUrl());
		if (provider == null) {
			return new ResponseEntity<ProxyConfigModel>(HttpStatus.BAD_REQUEST);
		}

		ProxyInfo info = new ProxyInfo(proxyPolicy, provider);
		HttpProxyConfig newProxy = HttpProxyConfigFactory.getHttpProxyConfig(info);
		newProxy.setActive(true);

		httpProxyConfigRepository.save(newProxy);

		return new ResponseEntity<ProxyConfigModel>(new ProxyConfigModel(newProxy), HttpStatus.CREATED);
	}

	@ApiOperation(value = "Update a proxy")
	@RequestMapping(value = "/proxy", method = RequestMethod.PATCH)
	public @ResponseBody ResponseEntity<ProxyConfigModel> addProxy(@RequestBody UpdateProxyModel updateProxy) {

		HttpProxyConfig proxy = httpProxyConfigRepository.findOne(updateProxy.getId());
		if (proxy == null) {
			return new ResponseEntity<ProxyConfigModel>(HttpStatus.NOT_FOUND);
		}

		if (UpdateProxyModel.ACTIVATE.equals(updateProxy.getAction())) {
			proxy.setActive(true);
			httpProxyConfigRepository.save(proxy);
		} else if (UpdateProxyModel.DEACTIVATE.equals(updateProxy.getAction())) {
			proxy.setActive(false);
			httpProxyConfigRepository.save(proxy);
		} else if (UpdateProxyModel.DELETE.equals(updateProxy.getAction())) {
			if (proxy.getType() == "NoProxy") {
				// No Proxy cannot be deleted
				return new ResponseEntity<ProxyConfigModel>(HttpStatus.UNAUTHORIZED);
			}
			httpProxyConfigRepository.delete(proxy);
		} else {
			return new ResponseEntity<ProxyConfigModel>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<ProxyConfigModel>(new ProxyConfigModel(proxy), HttpStatus.OK);
	}

	@ApiOperation(value = "Index of Policies")
	@RequestMapping(value = "/policy", method = RequestMethod.GET)
	public @ResponseBody List<PolicyConfig> policyIndex() {
		return policyConfigRepository.findAll();
	}

}
