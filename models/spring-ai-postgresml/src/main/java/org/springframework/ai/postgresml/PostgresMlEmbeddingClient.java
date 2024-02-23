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
package org.springframework.ai.postgresml;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.AbstractEmbeddingClient;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * <a href="https://postgresml.org">PostgresML</a> EmbeddingClient
 *
 * @author Toshiaki Maki
 * @author Christian Tzolov
 */
public class PostgresMlEmbeddingClient extends AbstractEmbeddingClient implements InitializingBean {

	public static final String DEFAULT_TRANSFORMER_MODEL = "distilbert-base-uncased";

	private final PostgresMlEmbeddingOptions defaultOptions;

	private final JdbcTemplate jdbcTemplate;

	public enum VectorType {

		PG_ARRAY("", null, (rs, i) -> {
			Array embedding = rs.getArray("embedding");
			return Arrays.stream((Float[]) embedding.getArray()).map(Float::doubleValue).toList();
		}),

		PG_VECTOR("::vector", "vector", (rs, i) -> {
			String embedding = rs.getString("embedding");
			return Arrays.stream((embedding.substring(1, embedding.length() - 1)
				/* remove leading '[' and trailing ']' */.split(","))).map(Double::parseDouble).toList();
		});

		private final String cast;

		private final String extensionName;

		private final RowMapper<List<Double>> rowMapper;

		VectorType(String cast, String extensionName, RowMapper<List<Double>> rowMapper) {
			this.cast = cast;
			this.extensionName = extensionName;
			this.rowMapper = rowMapper;
		}

	}

	/**
	 * a constructor
	 * @param jdbcTemplate JdbcTemplate
	 */
	public PostgresMlEmbeddingClient(JdbcTemplate jdbcTemplate) {
		this(jdbcTemplate, PostgresMlEmbeddingOptions.builder().build());
	}

	/**
	 * a PostgresMlEmbeddingClient constructor
	 * @param jdbcTemplate JdbcTemplate to use to interact with the database.
	 * @param options PostgresMlEmbeddingOptions to configure the client.
	 */
	public PostgresMlEmbeddingClient(JdbcTemplate jdbcTemplate, PostgresMlEmbeddingOptions options) {
		Assert.notNull(jdbcTemplate, "jdbc template must not be null.");
		Assert.notNull(options, "options must not be null.");
		Assert.notNull(options.getTransformer(), "transformer must not be null.");
		Assert.notNull(options.getVectorType(), "vectorType must not be null.");
		Assert.notNull(options.getKwargs(), "kwargs must not be null.");
		Assert.notNull(options.getMetadataMode(), "metadataMode must not be null.");

		this.jdbcTemplate = jdbcTemplate;
		this.defaultOptions = options;
	}

	/**
	 * a constructor
	 * @param jdbcTemplate JdbcTemplate
	 * @param transformer huggingface sentence-transformer name
	 */
	@Deprecated(since = "0.8.0", forRemoval = true)
	public PostgresMlEmbeddingClient(JdbcTemplate jdbcTemplate, String transformer) {
		this(jdbcTemplate, transformer, VectorType.PG_ARRAY);
	}

	/**
	 * a constructor
	 * @deprecated Use the constructor with {@link PostgresMlEmbeddingOptions} instead.
	 * @param jdbcTemplate JdbcTemplate
	 * @param transformer huggingface sentence-transformer name
	 * @param vectorType vector type in PostgreSQL
	 */
	@Deprecated(since = "0.8.0", forRemoval = true)
	public PostgresMlEmbeddingClient(JdbcTemplate jdbcTemplate, String transformer, VectorType vectorType) {
		this(jdbcTemplate, transformer, vectorType, Map.of(), MetadataMode.EMBED);
	}

	/**
	 * a constructor * @deprecated Use the constructor with
	 * {@link PostgresMlEmbeddingOptions} instead.
	 * @param jdbcTemplate JdbcTemplate
	 * @param transformer huggingface sentence-transformer name
	 * @param vectorType vector type in PostgreSQL
	 * @param kwargs optional arguments
	 */
	@Deprecated(since = "0.8.0", forRemoval = true)
	public PostgresMlEmbeddingClient(JdbcTemplate jdbcTemplate, String transformer, VectorType vectorType,
			Map<String, Object> kwargs, MetadataMode metadataMode) {
		Assert.notNull(jdbcTemplate, "jdbc template must not be null.");
		Assert.notNull(transformer, "transformer must not be null.");
		Assert.notNull(vectorType, "vectorType must not be null.");
		Assert.notNull(kwargs, "kwargs must not be null.");
		Assert.notNull(metadataMode, "metadataMode must not be null.");

		this.jdbcTemplate = jdbcTemplate;

		this.defaultOptions = PostgresMlEmbeddingOptions.builder()
			.withTransformer(transformer)
			.withVectorType(vectorType)
			.withMetadataMode(metadataMode)
			.withKwargs(ModelOptionsUtils.toJsonString(kwargs))
			.build();
	}

	@SuppressWarnings("null")
	@Override
	public List<Double> embed(String text) {
		return this.jdbcTemplate.queryForObject(
				"SELECT pgml.embed(?, ?, ?::JSONB)" + this.defaultOptions.getVectorType().cast + " AS embedding",
				this.defaultOptions.getVectorType().rowMapper, this.defaultOptions.getTransformer(), text,
				this.defaultOptions.getKwargs());
	}

	@Override
	public List<Double> embed(Document document) {
		return this.embed(document.getFormattedContent(this.defaultOptions.getMetadataMode()));
	}

	@SuppressWarnings("null")
	@Override
	public EmbeddingResponse call(EmbeddingRequest request) {

		final PostgresMlEmbeddingOptions optionsToUse = this.mergeOptions(request.getOptions());

		List<Embedding> data = new ArrayList<>();
		List<List<Double>> embed = List.of();

		List<String> texts = request.getInstructions();
		if (!CollectionUtils.isEmpty(texts)) {
			embed = this.jdbcTemplate.query(connection -> {
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT pgml.embed(?, text, ?::JSONB)"
						+ optionsToUse.getVectorType().cast + " AS embedding FROM (SELECT unnest(?) AS text) AS texts");
				preparedStatement.setString(1, optionsToUse.getTransformer());
				preparedStatement.setString(2, ModelOptionsUtils.toJsonString(optionsToUse.getKwargs()));
				preparedStatement.setArray(3, connection.createArrayOf("TEXT", texts.toArray(Object[]::new)));
				return preparedStatement;
			}, rs -> {
				List<List<Double>> result = new ArrayList<>();
				while (rs.next()) {
					result.add(optionsToUse.getVectorType().rowMapper.mapRow(rs, -1));
				}
				return result;
			});
		}

		if (!CollectionUtils.isEmpty(embed)) {
			for (int i = 0; i < embed.size(); i++) {
				data.add(new Embedding(embed.get(i), i));
			}
		}

		var metadata = new EmbeddingResponseMetadata(
				Map.of("transformer", optionsToUse.getTransformer(), "vector-type", optionsToUse.getVectorType().name(),
						"kwargs", ModelOptionsUtils.toJsonString(optionsToUse.getKwargs())));

		return new EmbeddingResponse(data, metadata);
	}

	/**
	 * Merge the default and request options.
	 * @param requestOptions request options to merge.
	 * @return the merged options.
	 */
	PostgresMlEmbeddingOptions mergeOptions(EmbeddingOptions requestOptions) {

		PostgresMlEmbeddingOptions options = (this.defaultOptions != null) ? this.defaultOptions
				: PostgresMlEmbeddingOptions.builder().build();

		if (requestOptions != null && !EmbeddingOptions.EMPTY.equals(requestOptions)) {
			options = ModelOptionsUtils.merge(requestOptions, options, PostgresMlEmbeddingOptions.class);
		}

		return options;
	}

	@Override
	public void afterPropertiesSet() {
		this.jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS pgml");
		this.jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS hstore");
		if (StringUtils.hasText(this.defaultOptions.getVectorType().extensionName)) {
			this.jdbcTemplate
				.execute("CREATE EXTENSION IF NOT EXISTS " + this.defaultOptions.getVectorType().extensionName);
		}
	}

}
