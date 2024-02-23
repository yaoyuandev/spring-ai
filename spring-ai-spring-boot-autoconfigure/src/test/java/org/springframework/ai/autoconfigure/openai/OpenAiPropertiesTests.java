/*
 * Copyright 2024 the original author or authors.
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

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.ResponseFormat;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.ToolChoice;
import org.springframework.ai.openai.api.OpenAiApi.FunctionTool.Type;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests for {@link OpenAiConnectionProperties}, {@link OpenAiChatProperties} and
 * {@link OpenAiEmbeddingProperties}.
 *
 * @author Christian Tzolov
 * @author Thomas Vitale
 * @since 0.8.0
 */
public class OpenAiPropertiesTests {

	@Test
	public void chatProperties() {

		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
				"spring.ai.openai.base-url=TEST_BASE_URL",
				"spring.ai.openai.api-key=abc123",
				"spring.ai.openai.chat.options.model=MODEL_XYZ",
				"spring.ai.openai.chat.options.temperature=0.55")
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var chatProperties = context.getBean(OpenAiChatProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getApiKey()).isEqualTo("abc123");
				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");

				assertThat(chatProperties.getApiKey()).isNull();
				assertThat(chatProperties.getBaseUrl()).isNull();

				assertThat(chatProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(chatProperties.getOptions().getTemperature()).isEqualTo(0.55f);
			});
	}

	@Test
	public void chatOverrideConnectionProperties() {

		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
				"spring.ai.openai.base-url=TEST_BASE_URL",
				"spring.ai.openai.api-key=abc123",
				"spring.ai.openai.chat.base-url=TEST_BASE_URL2",
				"spring.ai.openai.chat.api-key=456",
				"spring.ai.openai.chat.options.model=MODEL_XYZ",
				"spring.ai.openai.chat.options.temperature=0.55")
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var chatProperties = context.getBean(OpenAiChatProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getApiKey()).isEqualTo("abc123");
				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");

				assertThat(chatProperties.getApiKey()).isEqualTo("456");
				assertThat(chatProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL2");

				assertThat(chatProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(chatProperties.getOptions().getTemperature()).isEqualTo(0.55f);
			});
	}

	@Test
	public void embeddingProperties() {

		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
				"spring.ai.openai.base-url=TEST_BASE_URL",
				"spring.ai.openai.api-key=abc123",
				"spring.ai.openai.embedding.options.model=MODEL_XYZ")
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var embeddingProperties = context.getBean(OpenAiEmbeddingProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getApiKey()).isEqualTo("abc123");
				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");

				assertThat(embeddingProperties.getApiKey()).isNull();
				assertThat(embeddingProperties.getBaseUrl()).isNull();

				assertThat(embeddingProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
			});
	}

	@Test
	public void embeddingOverrideConnectionProperties() {

		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
				"spring.ai.openai.base-url=TEST_BASE_URL",
				"spring.ai.openai.api-key=abc123",
				"spring.ai.openai.embedding.base-url=TEST_BASE_URL2",
				"spring.ai.openai.embedding.api-key=456",
				"spring.ai.openai.embedding.options.model=MODEL_XYZ")
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var embeddingProperties = context.getBean(OpenAiEmbeddingProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getApiKey()).isEqualTo("abc123");
				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");

				assertThat(embeddingProperties.getApiKey()).isEqualTo("456");
				assertThat(embeddingProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL2");

				assertThat(embeddingProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
			});
	}

	@Test
	public void imageProperties() {
		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
						"spring.ai.openai.base-url=TEST_BASE_URL",
						"spring.ai.openai.api-key=abc123",
						"spring.ai.openai.image.options.model=MODEL_XYZ",
						"spring.ai.openai.image.options.n=3")
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var imageProperties = context.getBean(OpenAiImageProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getApiKey()).isEqualTo("abc123");
				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");

				assertThat(imageProperties.getApiKey()).isNull();
				assertThat(imageProperties.getBaseUrl()).isNull();

				assertThat(imageProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(imageProperties.getOptions().getN()).isEqualTo(3);
			});
	}

	@Test
	public void imageOverrideConnectionProperties() {
		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
						"spring.ai.openai.base-url=TEST_BASE_URL",
						"spring.ai.openai.api-key=abc123",
						"spring.ai.openai.image.base-url=TEST_BASE_URL2",
						"spring.ai.openai.image.api-key=456",
						"spring.ai.openai.image.options.model=MODEL_XYZ",
						"spring.ai.openai.image.options.n=3")
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var imageProperties = context.getBean(OpenAiImageProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getApiKey()).isEqualTo("abc123");
				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");

				assertThat(imageProperties.getApiKey()).isEqualTo("456");
				assertThat(imageProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL2");

				assertThat(imageProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(imageProperties.getOptions().getN()).isEqualTo(3);
			});
	}

	@Test
	public void chatOptionsTest() {

		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
				"spring.ai.openai.api-key=API_KEY",
				"spring.ai.openai.base-url=TEST_BASE_URL",

				"spring.ai.openai.chat.options.model=MODEL_XYZ",
				"spring.ai.openai.chat.options.frequencyPenalty=-1.5",
				"spring.ai.openai.chat.options.logitBias.myTokenId=-5",
				"spring.ai.openai.chat.options.maxTokens=123",
				"spring.ai.openai.chat.options.n=10",
				"spring.ai.openai.chat.options.presencePenalty=0",
				"spring.ai.openai.chat.options.responseFormat.type=json",
				"spring.ai.openai.chat.options.seed=66",
				"spring.ai.openai.chat.options.stop=boza,koza",
				"spring.ai.openai.chat.options.temperature=0.55",
				"spring.ai.openai.chat.options.topP=0.56",

				"spring.ai.openai.chat.options.toolChoice.functionName=toolChoiceFunctionName",

				"spring.ai.openai.chat.options.tools[0].function.name=myFunction1",
				"spring.ai.openai.chat.options.tools[0].function.description=function description",
				"spring.ai.openai.chat.options.tools[0].function.jsonSchema=" + """
					{
						"type": "object",
						"properties": {
							"location": {
								"type": "string",
								"description": "The city and state e.g. San Francisco, CA"
							},
							"lat": {
								"type": "number",
								"description": "The city latitude"
							},
							"lon": {
								"type": "number",
								"description": "The city longitude"
							},
							"unit": {
								"type": "string",
								"enum": ["c", "f"]
							}
						},
						"required": ["location", "lat", "lon", "unit"]
					}
					""",
					"spring.ai.openai.chat.options.user=userXYZ"
				)
			// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var chatProperties = context.getBean(OpenAiChatProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);
				var embeddingProperties = context.getBean(OpenAiEmbeddingProperties.class);

				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");
				assertThat(connectionProperties.getApiKey()).isEqualTo("API_KEY");

				assertThat(embeddingProperties.getOptions().getModel()).isEqualTo("text-embedding-ada-002");

				assertThat(chatProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(chatProperties.getOptions().getFrequencyPenalty()).isEqualTo(-1.5f);
				assertThat(chatProperties.getOptions().getLogitBias().get("myTokenId")).isEqualTo(-5);
				assertThat(chatProperties.getOptions().getMaxTokens()).isEqualTo(123);
				assertThat(chatProperties.getOptions().getN()).isEqualTo(10);
				assertThat(chatProperties.getOptions().getPresencePenalty()).isEqualTo(0);
				assertThat(chatProperties.getOptions().getResponseFormat()).isEqualTo(new ResponseFormat("json"));
				assertThat(chatProperties.getOptions().getSeed()).isEqualTo(66);
				assertThat(chatProperties.getOptions().getStop()).contains("boza", "koza");
				assertThat(chatProperties.getOptions().getTemperature()).isEqualTo(0.55f);
				assertThat(chatProperties.getOptions().getTopP()).isEqualTo(0.56f);

				assertThat(chatProperties.getOptions().getToolChoice())
					.isEqualTo(new ToolChoice("function", Map.of("name", "toolChoiceFunctionName")));
				assertThat(chatProperties.getOptions().getUser()).isEqualTo("userXYZ");

				assertThat(chatProperties.getOptions().getTools()).hasSize(1);
				var tool = chatProperties.getOptions().getTools().get(0);
				assertThat(tool.type()).isEqualTo(Type.FUNCTION);
				var function = tool.function();
				assertThat(function.name()).isEqualTo("myFunction1");
				assertThat(function.description()).isEqualTo("function description");
				assertThat(function.parameters()).isNotEmpty();
			});
	}

	@Test
	public void embeddingOptionsTest() {

		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
				"spring.ai.openai.api-key=API_KEY",
				"spring.ai.openai.base-url=TEST_BASE_URL",

				"spring.ai.openai.embedding.options.model=MODEL_XYZ",
				"spring.ai.openai.embedding.options.encodingFormat=MyEncodingFormat",
				"spring.ai.openai.embedding.options.user=userXYZ"
				)
			// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);
				var embeddingProperties = context.getBean(OpenAiEmbeddingProperties.class);

				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");
				assertThat(connectionProperties.getApiKey()).isEqualTo("API_KEY");

				assertThat(embeddingProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(embeddingProperties.getOptions().getEncodingFormat()).isEqualTo("MyEncodingFormat");
				assertThat(embeddingProperties.getOptions().getUser()).isEqualTo("userXYZ");
			});
	}

	@Test
	public void imageOptionsTest() {
		new ApplicationContextRunner().withPropertyValues(
		// @formatter:off
						"spring.ai.openai.api-key=API_KEY",
						"spring.ai.openai.base-url=TEST_BASE_URL",

						"spring.ai.openai.image.options.n=3",
						"spring.ai.openai.image.options.model=MODEL_XYZ",
						"spring.ai.openai.image.options.quality=hd",
						"spring.ai.openai.image.options.response_format=url",
						"spring.ai.openai.image.options.size=1024x1024",
						"spring.ai.openai.image.options.width=1024",
						"spring.ai.openai.image.options.height=1024",
						"spring.ai.openai.image.options.style=vivid",
						"spring.ai.openai.image.options.user=userXYZ"
				)
				// @formatter:on
			.withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class, OpenAiAutoConfiguration.class))
			.run(context -> {
				var imageProperties = context.getBean(OpenAiImageProperties.class);
				var connectionProperties = context.getBean(OpenAiConnectionProperties.class);

				assertThat(connectionProperties.getBaseUrl()).isEqualTo("TEST_BASE_URL");
				assertThat(connectionProperties.getApiKey()).isEqualTo("API_KEY");

				assertThat(imageProperties.getOptions().getN()).isEqualTo(3);
				assertThat(imageProperties.getOptions().getModel()).isEqualTo("MODEL_XYZ");
				assertThat(imageProperties.getOptions().getQuality()).isEqualTo("hd");
				assertThat(imageProperties.getOptions().getResponseFormat()).isEqualTo("url");
				assertThat(imageProperties.getOptions().getSize()).isEqualTo("1024x1024");
				assertThat(imageProperties.getOptions().getWidth()).isEqualTo(1024);
				assertThat(imageProperties.getOptions().getHeight()).isEqualTo(1024);
				assertThat(imageProperties.getOptions().getStyle()).isEqualTo("vivid");
				assertThat(imageProperties.getOptions().getUser()).isEqualTo("userXYZ");
			});
	}

}
