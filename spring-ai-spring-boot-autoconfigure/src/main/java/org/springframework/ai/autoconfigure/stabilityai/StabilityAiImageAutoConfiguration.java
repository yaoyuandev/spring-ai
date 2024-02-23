/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ai.autoconfigure.stabilityai;

import org.springframework.ai.stabilityai.StabilityAiImageClient;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Mark Pollack
 * @since 0.8.0
 */

@AutoConfiguration(after = { RestClientAutoConfiguration.class })
@ConditionalOnClass(StabilityAiApi.class)
@EnableConfigurationProperties({ StabilityAiConnectionProperties.class, StabilityAiImageProperties.class })
public class StabilityAiImageAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public StabilityAiApi stabilityAiApi(StabilityAiConnectionProperties commonProperties,
			StabilityAiImageProperties imageProperties) {

		String apiKey = StringUtils.hasText(imageProperties.getApiKey()) ? imageProperties.getApiKey()
				: commonProperties.getApiKey();

		String baseUrl = StringUtils.hasText(imageProperties.getBaseUrl()) ? imageProperties.getBaseUrl()
				: commonProperties.getBaseUrl();

		Assert.hasText(apiKey, "StabilityAI API key must be set");
		Assert.hasText(baseUrl, "StabilityAI base URL must be set");

		return new StabilityAiApi(apiKey, imageProperties.getOptions().getModel(), baseUrl);
	}

	@Bean
	@ConditionalOnMissingBean
	public StabilityAiImageClient stabilityAiImageClient(StabilityAiApi stabilityAiApi,
			StabilityAiImageProperties stabilityAiImageProperties) {
		return new StabilityAiImageClient(stabilityAiApi, stabilityAiImageProperties.getOptions());
	}

}
