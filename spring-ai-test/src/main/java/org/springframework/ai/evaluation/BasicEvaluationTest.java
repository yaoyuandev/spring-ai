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

package org.springframework.ai.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BasicEvaluationTest {

	private static final Logger logger = LoggerFactory.getLogger(BasicEvaluationTest.class);

	@Autowired
	protected ChatClient openAiChatClient;

	@Value("classpath:/prompts/spring/test/evaluation/qa-evaluator-accurate-answer.st")
	protected Resource qaEvaluatorAccurateAnswerResource;

	@Value("classpath:/prompts/spring/test/evaluation/qa-evaluator-not-related-message.st")
	protected Resource qaEvaluatorNotRelatedResource;

	@Value("classpath:/prompts/spring/test/evaluation/qa-evaluator-fact-based-answer.st")
	protected Resource qaEvaluatorFactBasedAnswerResource;

	@Value("classpath:/prompts/spring/test/evaluation/user-evaluator-message.st")
	protected Resource userEvaluatorResource;

	protected void evaluateQuestionAndAnswer(String question, ChatResponse response, boolean factBased) {
		assertThat(response).isNotNull();
		String answer = response.getResult().getOutput().getContent();
		logger.info("Question: " + question);
		logger.info("Answer:" + answer);
		PromptTemplate userPromptTemplate = new PromptTemplate(userEvaluatorResource,
				Map.of("question", question, "answer", answer));
		SystemMessage systemMessage;
		if (factBased) {
			systemMessage = new SystemMessage(qaEvaluatorFactBasedAnswerResource);
		}
		else {
			systemMessage = new SystemMessage(qaEvaluatorAccurateAnswerResource);
		}
		Message userMessage = userPromptTemplate.createMessage();
		Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
		String yesOrNo = openAiChatClient.call(prompt).getResult().getOutput().getContent();
		logger.info("Is Answer related to question: " + yesOrNo);
		if (yesOrNo.equalsIgnoreCase("no")) {
			SystemMessage notRelatedSystemMessage = new SystemMessage(qaEvaluatorNotRelatedResource);
			prompt = new Prompt(List.of(userMessage, notRelatedSystemMessage));
			String reasonForFailure = openAiChatClient.call(prompt).getResult().getOutput().getContent();
			fail(reasonForFailure);
		}
		else {
			logger.info("Answer is related to question.");
			assertThat(yesOrNo).isEqualTo("YES");
		}
	}

}