/*
 * Copyright 2023 the original author or authors.
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

package org.springframework.ai.autoconfigure.openai;

import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(OpenAiChatProperties.CONFIG_PREFIX)
public class OpenAiChatProperties extends OpenAiParentProperties {

	public static final String CONFIG_PREFIX = "spring.ai.openai.chat";

	public static final String DEFAULT_CHAT_MODEL = "gpt-3.5-turbo";

	private static final Double DEFAULT_TEMPERATURE = 0.7;

	@NestedConfigurationProperty
	private OpenAiChatOptions options = OpenAiChatOptions.builder()
		.withModel(DEFAULT_CHAT_MODEL)
		.withTemperature(DEFAULT_TEMPERATURE.floatValue())
		.build();

	public OpenAiChatOptions getOptions() {
		return options;
	}

	public void setOptions(OpenAiChatOptions options) {
		this.options = options;
	}

}
