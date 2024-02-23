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

import com.azure.ai.openai.OpenAIClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Christian Tzolov
 * @since 0.8.0
 */
public class AzureEmbeddingsOptionsTests {

	@Test
	public void createRequestWithChatOptions() {

		OpenAIClient mockClient = Mockito.mock(OpenAIClient.class);
		var client = new AzureOpenAiEmbeddingClient(mockClient, MetadataMode.EMBED,
				AzureOpenAiEmbeddingOptions.builder().withModel("DEFAULT_MODEL").withUser("USER_TEST").build());

		var requestOptions = client.toEmbeddingOptions(new EmbeddingRequest(List.of("Test message content"), null));

		assertThat(requestOptions.getInput()).hasSize(1);

		assertThat(requestOptions.getModel()).isEqualTo("DEFAULT_MODEL");
		assertThat(requestOptions.getUser()).isEqualTo("USER_TEST");

		requestOptions = client.toEmbeddingOptions(new EmbeddingRequest(List.of("Test message content"),
				AzureOpenAiEmbeddingOptions.builder().withModel("PROMPT_MODEL").withUser("PROMPT_USER").build()));

		assertThat(requestOptions.getInput()).hasSize(1);

		assertThat(requestOptions.getModel()).isEqualTo("PROMPT_MODEL");
		assertThat(requestOptions.getUser()).isEqualTo("PROMPT_USER");
	}

}
