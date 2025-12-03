package com.microservice.anamnesis.application.clients;

import com.microservice.anamnesis.domain.model.valueobjects.MedicalKnowledge;

import java.util.List;

/**
 * Client interface for accessing Ayllucare medical knowledge base.
 * This service retrieves relevant medical information from the proprietary dataset.
 */
public interface KnowledgeBaseClient {

    /**
     * Search for relevant medical knowledge based on symptoms and context.
     *
     * @param query The search query (symptoms, medical terms, patient concerns)
     * @param maxResults Maximum number of results to return
     * @return List of relevant medical knowledge entries
     */
    List<MedicalKnowledge> searchKnowledge(String query, int maxResults);

    /**
     * Check if the knowledge base service is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();
}

