package com.kinancity.server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kinancity.core.proxy.ProxyInfo;
import com.kinancity.core.proxy.impl.NoProxy;
import com.kinancity.core.proxy.policies.NintendoTimeLimitPolicy;
import com.kinancity.core.proxy.policies.UnlimitedUsePolicy;
import com.kinancity.server.entity.HttpProxyConfig;
import com.kinancity.server.entity.PolicyConfig;
import com.kinancity.server.entity.factory.HttpProxyConfigFactory;
import com.kinancity.server.entity.mapper.PolicyConfigMapper;
import com.kinancity.server.entity.repository.HttpProxyConfigRepository;
import com.kinancity.server.entity.repository.PolicyConfigRepository;

/**
 * Initial / Default configuration
 * 
 * @author drallieiv
 *
 */
@Service
public class DefaultConfigService implements InitializingBean {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HttpProxyConfigRepository httpProxyConfigRepository;
	
	@Autowired
	private PolicyConfigRepository policyConfigRepository;

	@Autowired
	private PolicyConfigMapper policyConfigMapper;

	@Override
	public void afterPropertiesSet() throws Exception {
		
		// Check Proxy configurations in Database
		List<HttpProxyConfig> proxies = httpProxyConfigRepository.findAll();
		logger.info("{} proxy configuration in DB", proxies.size());
		if (proxies.isEmpty()) {
			logger.info("Add direct connection by default");
			ProxyInfo directConnection = new ProxyInfo(new NintendoTimeLimitPolicy(), new NoProxy());
			HttpProxyConfig config = HttpProxyConfigFactory.getHttpProxyConfig(directConnection);
			config.setActive(true);
			httpProxyConfigRepository.save(config);
		}
		
		// Check Policies in Database
		List<PolicyConfig> policies = policyConfigRepository.findAll();
		logger.info("{} policies in DB", policies.size());
		if(policies.isEmpty()){
			logger.info("Add standard policies");
			
			PolicyConfig unlimitedPolicyConfig = policyConfigMapper.getPolicyConfig(new UnlimitedUsePolicy());
			unlimitedPolicyConfig.setName("Unlimited policy");
			unlimitedPolicyConfig.setDefaultConfig(true);
			policies.add(unlimitedPolicyConfig);
			
			PolicyConfig nintendoPolicyConfig = policyConfigMapper.getPolicyConfig(new NintendoTimeLimitPolicy());
			nintendoPolicyConfig.setName("Default Nintendo time limit policy");
			nintendoPolicyConfig.setDefaultConfig(true);
			policies.add(nintendoPolicyConfig);
			
			policyConfigRepository.save(policies);
		}
		
	}

}
