package com.microservice.anamnesis.infrastructure.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.anamnesis.application.clients.KnowledgeBaseClient;
import com.microservice.anamnesis.domain.model.valueobjects.MedicalKnowledge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of KnowledgeBaseClient that loads Ayllucare's medical dataset from JSON.
 *
 * Features:
 * - Loads medical knowledge from external JSON file (easy to maintain)
 * - Simple keyword-based search algorithm
 * - In-memory storage for fast access
 *
 * Future improvements:
 * - Migrate to vector database (Pinecone, Weaviate, Milvus)
 * - Implement semantic search with embeddings
 * - Add caching layer
 * - Support for multiple languages
 */
@Component
public class InMemoryKnowledgeBaseClient implements KnowledgeBaseClient {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryKnowledgeBaseClient.class);
    private static final String DATASET_FILE = "medical-knowledge-dataset.json";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<MedicalKnowledge> knowledgeBase;

    @PostConstruct
    public void init() {
        logger.info("Initializing Ayllucare medical knowledge base from JSON...");
        try {
            knowledgeBase = loadFromJson();
            logger.info("✅ Knowledge base loaded successfully with {} entries from {}",
                       knowledgeBase.size(), DATASET_FILE);
        } catch (Exception e) {
            logger.error("❌ Failed to load knowledge base from JSON: {}", e.getMessage());
            knowledgeBase = new ArrayList<>();
            logger.error("⚠️ Knowledge base is empty - system will not work properly!");
        }
    }

    @Override
    public List<MedicalKnowledge> searchKnowledge(String query, int maxResults) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        logger.debug("Searching knowledge base for: {}", query);

        String normalizedQuery = query.toLowerCase().trim();

        // Simple keyword matching (in production, use embeddings and semantic search)
        List<MedicalKnowledge> results = knowledgeBase.stream()
                .map(knowledge -> {
                    double score = calculateRelevanceScore(knowledge, normalizedQuery);
                    return new MedicalKnowledge(
                            knowledge.topic(),
                            knowledge.description(),
                            knowledge.symptoms(),
                            knowledge.recommendations(),
                            knowledge.redFlags(),
                            score
                    );
                })
                .filter(knowledge -> knowledge.relevanceScore() > 0)
                .sorted((a, b) -> Double.compare(b.relevanceScore(), a.relevanceScore()))
                .limit(maxResults)
                .collect(Collectors.toList());

        logger.debug("Found {} relevant knowledge entries", results.size());
        return results;
    }

    @Override
    public boolean isAvailable() {
        return knowledgeBase != null && !knowledgeBase.isEmpty();
    }

    /**
     * Loads medical knowledge from JSON file in resources folder.
     * File location: src/main/resources/medical-knowledge-dataset.json
     */
    private List<MedicalKnowledge> loadFromJson() throws IOException {
        ClassPathResource resource = new ClassPathResource(DATASET_FILE);

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode knowledgeArray = rootNode.get("knowledgeBase");

            if (knowledgeArray == null || !knowledgeArray.isArray()) {
                throw new IOException("Invalid JSON format: 'knowledgeBase' array not found");
            }

            List<MedicalKnowledge> dataset = new ArrayList<>();

            for (JsonNode node : knowledgeArray) {
                try {
                    String topic = node.get("topic").asText();
                    String description = node.get("description").asText();

                    List<String> symptoms = extractStringList(node, "symptoms");
                    List<String> recommendations = extractStringList(node, "recommendations");
                    List<String> redFlags = extractStringList(node, "redFlags");

                    dataset.add(new MedicalKnowledge(
                        topic,
                        description,
                        symptoms,
                        recommendations,
                        redFlags,
                        0.0
                    ));

                    logger.trace("Loaded: {}", topic);
                } catch (Exception e) {
                    logger.warn("Skipping invalid entry in JSON: {}", e.getMessage());
                }
            }

            logger.debug("Successfully loaded {} medical conditions from JSON", dataset.size());
            return dataset;
        }
    }

    /**
     * Helper method to extract string arrays from JSON node.
     */
    private List<String> extractStringList(JsonNode node, String fieldName) {
        List<String> result = new ArrayList<>();
        JsonNode arrayNode = node.get(fieldName);

        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(item -> result.add(item.asText()));
        }

        return result;
    }

    /**
     * Calculates relevance score between a medical knowledge entry and search query.
     * Uses simple keyword matching with weighted scoring.
     *
     * Scoring weights:
     * - Topic match: 3.0 points
     * - Description match: 2.0 points
     * - Symptom match: 1.5 points
     * - Recommendation match: 1.0 points
     *
     * @param knowledge Medical knowledge entry
     * @param query Normalized search query
     * @return Relevance score (higher is more relevant)
     */
    private double calculateRelevanceScore(MedicalKnowledge knowledge, String query) {
        double score = 0.0;
        String[] queryTerms = query.split("\\s+");

        for (String term : queryTerms) {
            // Check topic
            if (knowledge.topic().toLowerCase().contains(term)) {
                score += 3.0;
            }

            // Check description
            if (knowledge.description().toLowerCase().contains(term)) {
                score += 2.0;
            }

            // Check symptoms
            for (String symptom : knowledge.symptoms()) {
                if (symptom.toLowerCase().contains(term)) {
                    score += 1.5;
                }
            }

            // Check recommendations
            for (String recommendation : knowledge.recommendations()) {
                if (recommendation.toLowerCase().contains(term)) {
                    score += 1.0;
                }
            }
        }

        return score;
    }
}

