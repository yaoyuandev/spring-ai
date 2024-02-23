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

package org.springframework.ai.bedrock.titan;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.ai.chat.prompt.ChatOptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Christian Tzolov
 * @since 0.8.0
 */
@JsonInclude(Include.NON_NULL)
public class BedrockTitanChatOptions implements ChatOptions {

	// @formatter:off
	/**
	 * The temperature value controls the randomness of the generated text.
	 */
	private @JsonProperty("temperature") Float temperature;

	/**
	 * The topP value controls the diversity of the generated text. Use a lower value to ignore less probable options.
	 */
	private @JsonProperty("topP") Float topP;

	/**
	 * Maximum number of tokens to generate.
	 */
	private @JsonProperty("maxTokenCount") Integer maxTokenCount;

	/**
	 * A list of tokens that the model should stop generating after.
	 */
	private @JsonProperty("stopSequences") List<String> stopSequences;
	// @formatter:on

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private BedrockTitanChatOptions options = new BedrockTitanChatOptions();

		public Builder withTemperature(Float temperature) {
			this.options.temperature = temperature;
			return this;
		}

		public Builder withTopP(Float topP) {
			this.options.topP = topP;
			return this;
		}

		public Builder withMaxTokenCount(Integer maxTokenCount) {
			this.options.maxTokenCount = maxTokenCount;
			return this;
		}

		public Builder withStopSequences(List<String> stopSequences) {
			this.options.stopSequences = stopSequences;
			return this;
		}

		public BedrockTitanChatOptions build() {
			return this.options;
		}

	}

	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public Float getTopP() {
		return topP;
	}

	public void setTopP(Float topP) {
		this.topP = topP;
	}

	public Integer getMaxTokenCount() {
		return maxTokenCount;
	}

	public void setMaxTokenCount(Integer maxTokenCount) {
		this.maxTokenCount = maxTokenCount;
	}

	public List<String> getStopSequences() {
		return stopSequences;
	}

	public void setStopSequences(List<String> stopSequences) {
		this.stopSequences = stopSequences;
	}

	@Override
	public Integer getTopK() {
		throw new UnsupportedOperationException("Bedrock Titian Chat does not support the 'TopK' option.");
	}

	@Override
	public void setTopK(Integer topK) {
		throw new UnsupportedOperationException("Bedrock Titian Chat does not support the 'TopK' option.'");
	}

}
