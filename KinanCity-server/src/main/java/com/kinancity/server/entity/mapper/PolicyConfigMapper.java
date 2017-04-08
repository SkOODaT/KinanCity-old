package com.kinancity.server.entity.mapper;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.mapstruct.Mapper;

import com.kinancity.core.proxy.policies.ProxyPolicy;
import com.kinancity.core.proxy.policies.TimeLimitPolicy;
import com.kinancity.core.proxy.policies.UnlimitedUsePolicy;
import com.kinancity.server.entity.PolicyConfig;

@Mapper
public abstract class PolicyConfigMapper {

	public static final String TIME_POLICY_MAX_PER_PERIOD = "maxPerPeriod";
	public static final String TIME_POLICY_PERIOD_IN_SECONDS = "periodInSeconds";

	public PolicyConfig getPolicyConfig(ProxyPolicy policy) {
		PolicyConfig config = new PolicyConfig();
		config.setClassName(policy.getClass().getSimpleName());
		if (policy instanceof UnlimitedUsePolicy) {
			config.setJsonConfig(Json.createObjectBuilder().build().toString());
		} else if (policy instanceof TimeLimitPolicy) {
			TimeLimitPolicy timeLimitPolicy = TimeLimitPolicy.class.cast(policy);
			JsonObjectBuilder json = Json.createObjectBuilder();
			json.add(TIME_POLICY_MAX_PER_PERIOD, timeLimitPolicy.getMaxPerPeriod());
			json.add(TIME_POLICY_PERIOD_IN_SECONDS, timeLimitPolicy.getPeriodInSeconds());
			config.setJsonConfig(json.build().toString());
		}
		return config;
	}

	public ProxyPolicy getProxyPolicy(PolicyConfig config) {
		if (config.getClassName() == TimeLimitPolicy.class.getSimpleName()) {
			JsonObject jsonConfig = Json.createReader(new StringReader(config.getJsonConfig())).readObject();
			return new TimeLimitPolicy(jsonConfig.getInt(TIME_POLICY_MAX_PER_PERIOD), jsonConfig.getInt(TIME_POLICY_PERIOD_IN_SECONDS));
		} else if (config.getClassName() == UnlimitedUsePolicy.class.getSimpleName()) {
			return new UnlimitedUsePolicy();
		} else {
			return null;
		}
	}
}
