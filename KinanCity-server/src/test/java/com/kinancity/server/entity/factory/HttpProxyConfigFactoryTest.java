package com.kinancity.server.entity.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kinancity.core.proxy.policies.ProxyPolicy;
import com.kinancity.core.proxy.policies.TimeLimitPolicy;
import com.kinancity.server.errors.ConfigurationLoadingException;

public class HttpProxyConfigFactoryTest {

	private static final Logger logger = LoggerFactory.getLogger(HttpProxyConfigFactoryTest.class);

	@Test
	public void policyToStringTest() throws ConfigurationLoadingException {
		ProxyPolicy proxyPolicy = new TimeLimitPolicy(5, 10 * 60);

		// From Policy to Serialized string
		String strPolicy = HttpProxyConfigFactory.toString(proxyPolicy);
		logger.info("Policy [{}] serialized as [{}]", proxyPolicy, strPolicy);
		assertThat(strPolicy).isEqualTo("TimeLimitPolicy:5:600");

		// From Serialized string to Policy
		ProxyPolicy policy = HttpProxyConfigFactory.toPolicy(strPolicy);
		assertThat(policy).isInstanceOf(TimeLimitPolicy.class);
		assertThat(((TimeLimitPolicy) policy).getMaxPerPeriod()).isEqualTo(5);
		assertThat(((TimeLimitPolicy) policy).getPeriodInSeconds()).isEqualTo(10 * 60);

	}

}
