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

package org.springframework.ai.azure.openai;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.ai.chat.prompt.ChatOptions;

/**
 * The configuration information for a chat completions request. Completions support a
 * wide variety of tasks and generate text that continues from or "completes" provided
 * prompt data.
 *
 * @author Christian Tzolov
 */
@JsonInclude(Include.NON_NULL)
public class AzureOpenAiChatOptions implements ChatOptions {

	/**
	 * The maximum number of tokens to generate.
	 */
	@JsonProperty(value = "max_tokens")
	private Integer maxTokens;

	/**
	 * The sampling temperature to use that controls the apparent creativity of generated
	 * completions. Higher values will make output more random while lower values will
	 * make results more focused and deterministic. It is not recommended to modify
	 * temperature and top_p for the same completions request as the interaction of these
	 * two settings is difficult to predict.
	 */
	@JsonProperty(value = "temperature")
	private Float temperature;

	/**
	 * An alternative to sampling with temperature called nucleus sampling. This value
	 * causes the model to consider the results of tokens with the provided probability
	 * mass. As an example, a value of 0.15 will cause only the tokens comprising the top
	 * 15% of probability mass to be considered. It is not recommended to modify
	 * temperature and top_p for the same completions request as the interaction of these
	 * two settings is difficult to predict.
	 */
	@JsonProperty(value = "top_p")
	private Float topP;

	/**
	 * A map between GPT token IDs and bias scores that influences the probability of
	 * specific tokens appearing in a completions response. Token IDs are computed via
	 * external tokenizer tools, while bias scores reside in the range of -100 to 100 with
	 * minimum and maximum values corresponding to a full ban or exclusive selection of a
	 * token, respectively. The exact behavior of a given bias score varies by model.
	 */
	@JsonProperty(value = "logit_bias")
	private Map<String, Integer> logitBias;

	/**
	 * An identifier for the caller or end user of the operation. This may be used for
	 * tracking or rate-limiting purposes.
	 */
	@JsonProperty(value = "user")
	private String user;

	/**
	 * The number of chat completions choices that should be generated for a chat
	 * completions response. Because this setting can generate many completions, it may
	 * quickly consume your token quota. Use carefully and ensure reasonable settings for
	 * max_tokens and stop.
	 */
	@JsonProperty(value = "n")
	private Integer n;

	/**
	 * A collection of textual sequences that will end completions generation.
	 */
	@JsonProperty(value = "stop")
	private List<String> stop;

	/**
	 * A value that influences the probability of generated tokens appearing based on
	 * their existing presence in generated text. Positive values will make tokens less
	 * likely to appear when they already exist and increase the model's likelihood to
	 * output new topics.
	 */
	@JsonProperty(value = "presence_penalty")
	private Double presencePenalty;

	/**
	 * A value that influences the probability of generated tokens appearing based on
	 * their cumulative frequency in generated text. Positive values will make tokens less
	 * likely to appear as their frequency increases and decrease the likelihood of the
	 * model repeating the same statements verbatim.
	 */
	@JsonProperty(value = "frequency_penalty")
	private Double frequencyPenalty;

	/**
	 * The model name to provide as part of this completions request. Not applicable to
	 * Azure OpenAI, where deployment information should be included in the Azure resource
	 * URI that's connected to.
	 */
	@JsonProperty(value = "model")
	private String model;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		protected AzureOpenAiChatOptions options;

		public Builder() {
			this.options = new AzureOpenAiChatOptions();
		}

		public Builder(AzureOpenAiChatOptions options) {
			this.options = options;
		}

		public Builder withModel(String model) {
			this.options.model = model;
			return this;
		}

		public Builder withFrequencyPenalty(Float frequencyPenalty) {
			this.options.frequencyPenalty = frequencyPenalty.doubleValue();
			return this;
		}

		public Builder withLogitBias(Map<String, Integer> logitBias) {
			this.options.logitBias = logitBias;
			return this;
		}

		public Builder withMaxTokens(Integer maxTokens) {
			this.options.maxTokens = maxTokens;
			return this;
		}

		public Builder withN(Integer n) {
			this.options.n = n;
			return this;
		}

		public Builder withPresencePenalty(Float presencePenalty) {
			this.options.presencePenalty = presencePenalty.doubleValue();
			return this;
		}

		public Builder withStop(List<String> stop) {
			this.options.stop = stop;
			return this;
		}

		public Builder withTemperature(Float temperature) {
			this.options.temperature = temperature;
			return this;
		}

		public Builder withTopP(Float topP) {
			this.options.topP = topP;
			return this;
		}

		public Builder withUser(String user) {
			this.options.user = user;
			return this;
		}

		public AzureOpenAiChatOptions build() {
			return this.options;
		}

	}

	public Integer getMaxTokens() {
		return this.maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Map<String, Integer> getLogitBias() {
		return this.logitBias;
	}

	public void setLogitBias(Map<String, Integer> logitBias) {
		this.logitBias = logitBias;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getN() {
		return this.n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	public List<String> getStop() {
		return this.stop;
	}

	public void setStop(List<String> stop) {
		this.stop = stop;
	}

	public Double getPresencePenalty() {
		return this.presencePenalty;
	}

	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	public Double getFrequencyPenalty() {
		return this.frequencyPenalty;
	}

	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public Float getTemperature() {
		return this.temperature;
	}

	@Override
	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	@Override
	public Float getTopP() {
		return this.topP;
	}

	@Override
	public void setTopP(Float topP) {
		this.topP = topP;
	}

	@Override
	@JsonIgnore
	public Integer getTopK() {
		throw new UnsupportedOperationException("Unimplemented method 'getTopK'");
	}

	@Override
	@JsonIgnore
	public void setTopK(Integer topK) {
		throw new UnsupportedOperationException("Unimplemented method 'setTopK'");
	}

}
