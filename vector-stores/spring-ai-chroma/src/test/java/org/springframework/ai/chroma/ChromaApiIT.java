/*
 * Copyright 2023-2023 the original author or authors.
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

package org.springframework.ai.chroma;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.chroma.ChromaApi.AddEmbeddingsRequest;
import org.springframework.ai.chroma.ChromaApi.Collection;
import org.springframework.ai.chroma.ChromaApi.GetEmbeddingsRequest;
import org.springframework.ai.chroma.ChromaApi.QueryRequest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Christian Tzolov
 */
@SpringBootTest
@Testcontainers
public class ChromaApiIT {

	@Container
	static GenericContainer<?> chromaContainer = new GenericContainer<>("ghcr.io/chroma-core/chroma:0.4.22.dev44")
		.withExposedPorts(8000);

	@Autowired
	ChromaApi chroma;

	@BeforeEach
	public void beforeEach() {
		chroma.listCollections().stream().forEach(c -> chroma.deleteCollection(c.name()));
	}

	@Test
	public void testClient() {
		var newCollection = chroma.createCollection(new ChromaApi.CreateCollectionRequest("TestCollection"));
		assertThat(newCollection).isNotNull();
		assertThat(newCollection.name()).isEqualTo("TestCollection");

		var getCollection = chroma.getCollection("TestCollection");
		assertThat(getCollection).isNotNull();
		assertThat(getCollection.name()).isEqualTo("TestCollection");
		assertThat(getCollection.id()).isEqualTo(newCollection.id());

		List<Collection> collections = chroma.listCollections();
		assertThat(collections).hasSize(1);
		assertThat(collections.get(0).id()).isEqualTo(newCollection.id());

		chroma.deleteCollection(newCollection.name());
		assertThat(chroma.listCollections()).hasSize(0);
	}

	@Test
	public void testCollection() {
		var newCollection = chroma.createCollection(new ChromaApi.CreateCollectionRequest("TestCollection"));
		assertThat(chroma.countEmbeddings(newCollection.id())).isEqualTo(0);

		var addEmbeddingRequest = new AddEmbeddingsRequest(List.of("id1", "id2"),
				List.of(new float[] { 1f, 1f, 1f }, new float[] { 2f, 2f, 2f }),
				List.of(Map.of(), Map.of("key1", "value1", "key2", true, "key3", 23.4)),
				List.of("Hello World", "Big World"));

		chroma.upsertEmbeddings(newCollection.id(), addEmbeddingRequest);

		var addEmbeddingRequest2 = new AddEmbeddingsRequest("id3", new float[] { 3f, 3f, 3f },
				Map.of("key1", "value1", "key2", true, "key3", 23.4), "Big World");

		chroma.upsertEmbeddings(newCollection.id(), addEmbeddingRequest2);

		assertThat(chroma.countEmbeddings(newCollection.id())).isEqualTo(3);

		var queryResult = chroma.queryCollection(newCollection.id(),
				new QueryRequest(List.of(1f, 1f, 1f), 3, chroma.where("""
						{
							"key2" : { "$eq": true }
						}
						""")));
		assertThat(queryResult.ids().get(0)).hasSize(2);
		assertThat(queryResult.ids().get(0)).containsExactlyInAnyOrder("id2", "id3");

		// Update existing embedding.
		chroma.upsertEmbeddings(newCollection.id(), new AddEmbeddingsRequest("id3", new float[] { 6f, 6f, 6f },
				Map.of("key1", "value2", "key2", false, "key4", 23.4), "Small World"));

		var result = chroma.getEmbeddings(newCollection.id(), new GetEmbeddingsRequest(List.of("id2")));
		assertThat(result.ids().get(0)).isEqualTo("id2");

		queryResult = chroma.queryCollection(newCollection.id(),
				new QueryRequest(List.of(1f, 1f, 1f), 3, chroma.where("""
						{
							"key2" : { "$eq": true }
						}
						""")));
		assertThat(queryResult.ids().get(0)).hasSize(1);
		assertThat(queryResult.ids().get(0)).containsExactlyInAnyOrder("id2");
	}

	@Test
	public void testQueryWhere() {

		var collection = chroma.createCollection(new ChromaApi.CreateCollectionRequest("TestCollection"));

		var add1 = new AddEmbeddingsRequest("id1", new float[] { 1f, 1f, 1f },
				Map.of("country", "BG", "active", true, "price", 23.4, "year", 2020),
				"The World is Big and Salvation Lurks Around the Corner");

		var add2 = new AddEmbeddingsRequest("id2", new float[] { 1f, 1f, 1f }, Map.of("country", "NL"),
				"The World is Big and Salvation Lurks Around the Corner");

		var add3 = new AddEmbeddingsRequest("id3", new float[] { 1f, 1f, 1f },
				Map.of("country", "BG", "active", false, "price", 40.1, "year", 2023),
				"The World is Big and Salvation Lurks Around the Corner");

		chroma.upsertEmbeddings(collection.id(), add1);
		chroma.upsertEmbeddings(collection.id(), add2);
		chroma.upsertEmbeddings(collection.id(), add3);

		assertThat(chroma.countEmbeddings(collection.id())).isEqualTo(3);

		var queryResult = chroma.queryCollection(collection.id(), new QueryRequest(List.of(1f, 1f, 1f), 3));

		assertThat(queryResult.ids().get(0)).hasSize(3);
		assertThat(queryResult.ids().get(0)).containsExactlyInAnyOrder("id1", "id2", "id3");

		var chromaEmbeddings = chroma.toEmbeddingResponseList(queryResult);

		assertThat(chromaEmbeddings).hasSize(3);
		assertThat(chromaEmbeddings).hasSize(3);

		queryResult = chroma.queryCollection(collection.id(), new QueryRequest(List.of(1f, 1f, 1f), 3, chroma.where("""
				{
					"$and" : [
						{"country" : { "$eq": "BG"}},
						{"year" : { "$gte": 2020}}
					]
				}
				""")));
		assertThat(queryResult.ids().get(0)).hasSize(2);
		assertThat(queryResult.ids().get(0)).containsExactlyInAnyOrder("id1", "id3");

		queryResult = chroma.queryCollection(collection.id(), new QueryRequest(List.of(1f, 1f, 1f), 3, chroma.where("""
				{
					"$and" : [
						{"country" : { "$eq": "BG"}},
						{"year" : { "$gte": 2020}},
						{"active" : { "$eq": true}}
					]
				}
				""")));
		assertThat(queryResult.ids().get(0)).hasSize(1);
		assertThat(queryResult.ids().get(0)).containsExactlyInAnyOrder("id1");
	}

	@SpringBootConfiguration
	public static class Config {

		@Bean
		public RestTemplate restTemplate() {
			return new RestTemplate();
		}

		@Bean
		public ChromaApi chromaApi(RestTemplate restTemplate) {
			String host = chromaContainer.getHost();
			int port = chromaContainer.getMappedPort(8000);
			String baseUrl = "http://%s:%d".formatted(host, port);
			return new ChromaApi(baseUrl, restTemplate);
		}

	}

}
