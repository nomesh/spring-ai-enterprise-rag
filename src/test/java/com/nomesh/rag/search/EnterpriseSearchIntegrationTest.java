package com.nomesh.rag.search;

import com.nomesh.rag.metadata.MetadataConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies enterprise semantic search against a real PGVector database.
 *
 * <p>The test uses a Testcontainers-managed PostgreSQL instance with the
 * PGVector extension while supplying a deterministic embedding model so the
 * integration test does not depend on a locally running Ollama instance.</p>
 *
 * @author Nomesh De Silva
 */
@Testcontainers
@SpringBootTest(
        properties = {
                "spring.ai.model.embedding=none",
                "spring.ai.vectorstore.pgvector.initialize-schema=true"
        }
)
@Import(EnterpriseSearchIntegrationTest.TestEmbeddingConfiguration.class)
class EnterpriseSearchIntegrationTest {

    /**
     * PGVector-enabled PostgreSQL container used by the integration test.
     */
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(
                    "pgvector/pgvector:pg16"
            )
                    .withDatabaseName("ragtest")
                    .withUsername("ragtest")
                    .withPassword("ragtest");

    @Autowired
    private VectorStore vectorStore;

    /**
     * Supplies container database properties to the Spring application context.
     *
     * @param registry Spring dynamic property registry
     */
    @DynamicPropertySource
    static void configureDatabase(
            DynamicPropertyRegistry registry
    ) {

        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );
    }

    /**
     * Ensures each test starts with a known vector dataset.
     */
    @BeforeEach
    void setUpDocuments() {

        vectorStore.add(
                List.of(
                        document(
                                "HR annual leave policy allows employees "
                                        + "to request paid annual leave.",
                                "HR",
                                "policy"
                        ),
                        document(
                                "Engineering deployment procedures require "
                                        + "peer review before production release.",
                                "Engineering",
                                "procedure"
                        ),
                        document(
                                "Security access policies require multi-factor "
                                        + "authentication for privileged users.",
                                "Security",
                                "policy"
                        )
                )
        );
    }

    @Test
    void shouldRetrieveSemanticallyRelevantDocumentFromPgVector() {

        List<Document> results =
                vectorStore.similaritySearch(
                        "annual leave employee policy"
                );

        assertFalse(results.isEmpty());

        assertTrue(
                results.stream()
                        .anyMatch(document ->
                                document.getText()
                                        .contains("annual leave")
                        )
        );
    }

    @Test
    void shouldSearchDocumentsStoredWithEnterpriseMetadata() {

        List<Document> results =
                vectorStore.similaritySearch(
                        "security authentication"
                );

        assertTrue(
                results.stream()
                        .anyMatch(document ->
                                "Security".equals(
                                        document.getMetadata()
                                                .get(
                                                        MetadataConstants.DEPARTMENT
                                                )
                                )
                        )
        );
    }

    private Document document(
            String content,
            String department,
            String classification
    ) {

        return new Document(
                content,
                Map.of(
                        MetadataConstants.DEPARTMENT,
                        department,
                        MetadataConstants.CLASSIFICATION,
                        classification,
                        MetadataConstants.SOURCE,
                        "integration-test"
                )
        );
    }

    /**
     * Provides deterministic embeddings for integration tests.
     */
    @TestConfiguration
    static class TestEmbeddingConfiguration {

        /**
         * Creates an embedding model that produces deterministic vectors based
         * on enterprise-domain keywords.
         *
         * @return deterministic embedding model
         */
        @Bean
        @Primary
        EmbeddingModel testEmbeddingModel() {

            return new EmbeddingModel() {

                @Override
                public EmbeddingResponse call(
                        EmbeddingRequest request
                ) {

                    List<Embedding> embeddings =
                            new ArrayList<>();

                    List<String> inputs =
                            request.getInstructions();

                    for (int i = 0; i < inputs.size(); i++) {

                        embeddings.add(
                                new Embedding(
                                        embedText(inputs.get(i)),
                                        i
                                )
                        );
                    }

                    return new EmbeddingResponse(
                            embeddings
                    );
                }

                @Override
                public float[] embed(Document document) {

                    return embedText(
                            document.getText()
                    );
                }

                @Override
                public int dimensions() {
                    return 3;
                }

                private float[] embedText(String text) {

                    String normalized =
                            text.toLowerCase();

                    float leaveScore =
                            containsAny(
                                    normalized,
                                    "leave",
                                    "annual",
                                    "employee",
                                    "holiday"
                            );

                    float engineeringScore =
                            containsAny(
                                    normalized,
                                    "engineering",
                                    "deployment",
                                    "production",
                                    "review"
                            );

                    float securityScore =
                            containsAny(
                                    normalized,
                                    "security",
                                    "authentication",
                                    "access",
                                    "privileged"
                            );

                    return new float[]{
                            leaveScore,
                            engineeringScore,
                            securityScore
                    };
                }

                private float containsAny(
                        String text,
                        String... terms
                ) {

                    for (String term : terms) {
                        if (text.contains(term)) {
                            return 1.0f;
                        }
                    }

                    return 0.01f;
                }
            };
        }
    }
}