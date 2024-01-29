/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.ai.stabilityai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.stabilityai.api.StabilityAiApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "STABILITYAI_API_KEY", matches = ".*")
public class StabilityAiApiIT {

	StabilityAiApi stabilityAiApi = new StabilityAiApi(System.getenv("STABILITYAI_API_KEY"));

	@Test
	void generateImage() throws IOException {

		List<StabilityAiApi.GenerateImageRequest.TextPrompts> textPrompts = List
			.of(new StabilityAiApi.GenerateImageRequest.TextPrompts(
					"A light cream colored mini golden doodle holding a sign that says 'Heading to BARCADE !'", 0.5f));
		var builder = StabilityAiApi.GenerateImageRequest.builder()
			.withTextPrompts(textPrompts)
			.withHeight(1024)
			.withWidth(1024)
			.withCfgScale(7f)
			.withSamples(1)
			.withSeed(123L)
			.withSteps(30)
			.withStylePreset("photographic");
		StabilityAiApi.GenerateImageRequest request = builder.build();
		StabilityAiApi.GenerateImageResponse response = stabilityAiApi.generateImage(request);

		assertThat(response).isNotNull();
		List<StabilityAiApi.GenerateImageResponse.Artifacts> artifacts = response.artifacts();
		writeToFile(artifacts);
		assertThat(artifacts).hasSize(1);
		var firstArtifact = artifacts.get(0);
		assertThat(firstArtifact.base64()).isNotEmpty();
		assertThat(firstArtifact.seed()).isPositive();
		assertThat(firstArtifact.finishReason()).isEqualTo("SUCCESS");

	}

	private static void writeToFile(List<StabilityAiApi.GenerateImageResponse.Artifacts> artifacts) throws IOException {
		int counter = 0;
		String systemTempDir = System.getProperty("java.io.tmpdir");
		for (StabilityAiApi.GenerateImageResponse.Artifacts artifact : artifacts) {
			counter++;
			byte[] imageBytes = Base64.getDecoder().decode(artifact.base64());
			String fileName = String.format("dog%d.png", counter);
			String filePath = systemTempDir + File.separator + fileName;
			File file = new File(filePath);
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(imageBytes);
			}
		}
	}

}
