package com.kinancity.server.entity.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kinancity.core.proxy.HttpProxyProvider;
import com.kinancity.core.proxy.ProxyInfo;
import com.kinancity.core.proxy.impl.HttpProxy;
import com.kinancity.core.proxy.impl.NoProxy;
import com.kinancity.core.proxy.policies.ProxyPolicy;
import com.kinancity.core.proxy.policies.TimeLimitPolicy;
import com.kinancity.core.proxy.policies.UnlimitedUsePolicy;
import com.kinancity.server.entity.HttpProxyConfig;
import com.kinancity.server.errors.ConfigurationLoadingException;

/**
 * Factory managing between server and core proxy info
 * @author drallieiv
 *
 */
public class HttpProxyConfigFactory {

	private static final String SERIALIZER_SEPARATOR = ":";

	private static final String POLICY_TIME_LIMIT = "TimeLimitPolicy";
	private static final String POLICY_UNLIMITED = "UnlimitedUsePolicy";

	private static Logger logger = LoggerFactory.getLogger(HttpProxyConfigFactory.class);

	/**
	 * Construct an entity from a Proxy Provider to be saved
	 * 
	 * @param provider
	 * @return
	 */
	public static HttpProxyConfig getHttpProxyConfig(ProxyInfo info) {
		HttpProxyConfig config = new HttpProxyConfig();

		HttpProxyProvider provider = info.getProvider();
		config.setType(provider.getClass().getSimpleName());
		if (provider instanceof HttpProxy) {
			HttpProxy proxyProvider = HttpProxy.class.cast(provider);
			config.setUri(proxyProvider.toURI());
		}
		config.setPolicy(toString(info.getProxyPolicy()));

		return config;
	}

	public static ProxyInfo toProxyInfo(HttpProxyConfig config) throws ConfigurationLoadingException {
		
		HttpProxyProvider provider;
		switch (config.getType()) {
		case "HttpProxy":
			provider = HttpProxy.fromURI(config.getUri());
			break;
		case "NoProxy":
			provider = new NoProxy();
			break;
		default:
			throw new ConfigurationLoadingException("Unknown proxy provider type ;" + config.getType());
		}
		
		ProxyPolicy proxyPolicy = toPolicy(config.getPolicy());
		
		return new ProxyInfo(proxyPolicy, provider);
	}

	/**
	 * Serialization to Policy from String
	 * 
	 * @param strPolicy
	 * @return
	 * @throws ConfigurationLoadingException
	 */
	public static ProxyPolicy toPolicy(String strPolicy) throws ConfigurationLoadingException {
		String[] parts = strPolicy.split(SERIALIZER_SEPARATOR);
		switch (parts[0]) {
		case POLICY_TIME_LIMIT:
			return toTimeLimitPolicy(parts);
		case POLICY_UNLIMITED:
			return new UnlimitedUsePolicy();
		default:
			logger.warn("Unknown policy type {}", parts[0]);
			return null;
		}
	}

	/**
	 * Serialization to String from Policy
	 * 
	 * @param proxyPolicy
	 * @return
	 */
	public static String toString(ProxyPolicy proxyPolicy) {
		switch (proxyPolicy.getClass().getSimpleName()) {
		case POLICY_TIME_LIMIT:
			return toString(TimeLimitPolicy.class.cast(proxyPolicy));
		case POLICY_UNLIMITED:
			return POLICY_UNLIMITED;
		default:
			return proxyPolicy.getClass().getSimpleName();
		}
	}

	/**
	 * un-serialize a TimeLimitPolicy
	 * 
	 * @param parts
	 *            name and attributes
	 * @return a TimeLimitPolicy
	 * @throws ConfigurationLoadingException
	 */
	private static TimeLimitPolicy toTimeLimitPolicy(String[] parts) throws ConfigurationLoadingException {
		if (parts.length < 3) {
			throw new ConfigurationLoadingException("Missing arguments for TimeLimitPolicy");
		}
		try {
			return new TimeLimitPolicy(Integer.parseInt(parts[1]), Long.parseLong(parts[2]));
		} catch (NumberFormatException e) {
			throw new ConfigurationLoadingException("Invalid arguments for TimeLimitPolicy", e);
		}
	}

	public static String toString(TimeLimitPolicy proxyPolicy) {
		return POLICY_TIME_LIMIT
				+ SERIALIZER_SEPARATOR
				+ proxyPolicy.getMaxPerPeriod()
				+ SERIALIZER_SEPARATOR
				+ proxyPolicy.getPeriodInSeconds();
	}
}
