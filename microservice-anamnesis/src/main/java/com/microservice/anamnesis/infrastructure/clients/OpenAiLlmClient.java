package com.microservice.anamnesis.infrastructure.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.anamnesis.application.clients.KnowledgeBaseClient;
import com.microservice.anamnesis.application.clients.LlmClient;
import com.microservice.anamnesis.application.dto.ProfileSnapshot;
import com.microservice.anamnesis.domain.model.aggregates.AnamnesisSession;
import com.microservice.anamnesis.domain.model.valueobjects.AnamnesisSummary;
import com.microservice.anamnesis.domain.model.valueobjects.ConversationMessage;
import com.microservice.anamnesis.domain.model.valueobjects.MedicalKnowledge;
import com.microservice.anamnesis.domain.model.valueobjects.SenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * LLM Client implementation supporting both OpenAI and Google Gemini.
 * Provider is selected via llm.provider property (openai | gemini).
 */
@Component
public class OpenAiLlmClient implements LlmClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiLlmClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final KnowledgeBaseClient knowledgeBaseClient;

    public OpenAiLlmClient(KnowledgeBaseClient knowledgeBaseClient) {
        this.knowledgeBaseClient = knowledgeBaseClient;
    }

    @Value("${llm.provider:gemini}")
    private String provider;

    // OpenAI Configuration
    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.model:gpt-4o}")
    private String openaiModel;

    @Value("${openai.enabled:false}")
    private boolean openaiEnabled;

    // Gemini Configuration
    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-pro}")
    private String geminiModel;

    @Value("${gemini.enabled:false}")
    private boolean geminiEnabled;

    @Value("${gemini.temperature:0.7}")
    private double temperature;

    @Value("${gemini.max.tokens:1000}")
    private int maxTokens;

    @Override
    public String generateResponse(AnamnesisSession session, ProfileSnapshot profile) {
        if (!isAvailable()) {
            logger.warn("LLM service is not available");
            return "Lo siento, el servicio de asistente no está disponible. Por favor, continúe describiendo sus síntomas.";
        }

        try {
            logger.info("Generating LLM response for session: {} using provider: {} with RAG", session.getId(), provider);

            // Step 1: Extract context from the latest patient message and session
            String searchQuery = extractSearchQuery(session);

            // Step 2: Query Ayllucare knowledge base (RAG - Retrieval)
            List<MedicalKnowledge> relevantKnowledge = List.of();
            if (knowledgeBaseClient.isAvailable()) {
                relevantKnowledge = knowledgeBaseClient.searchKnowledge(searchQuery, 3);
                logger.info("Retrieved {} relevant knowledge entries from Ayllucare dataset", relevantKnowledge.size());
            }

            // Step 3: Build enriched prompt with knowledge base context (RAG - Augmented Generation)
            String systemPrompt = buildSystemPrompt(profile, false, relevantKnowledge);
            String conversationContext = buildConversationContext(session);

            // Step 4: Generate response using LLM with augmented context
            if ("gemini".equalsIgnoreCase(provider)) {
                return callGeminiAPI(systemPrompt, conversationContext, false);
            } else {
                return callOpenAIAPI(systemPrompt, conversationContext, false);
            }

        } catch (Exception e) {
            logger.error("Error generating LLM response", e);
            return "Disculpe, tuve un problema al procesar su mensaje. ¿Podría reformularlo?";
        }
    }

    @Override
    public AnamnesisSummary generateSummary(AnamnesisSession session, ProfileSnapshot profile) {
        if (!isAvailable()) {
            logger.warn("LLM service is not available for summary generation");
            return createFallbackSummary(session, profile);
        }

        try {
            logger.info("Generating anamnesis summary for session: {} using provider: {} with RAG", session.getId(), provider);

            // Query knowledge base for summary context
            String searchQuery = extractSearchQuery(session);
            List<MedicalKnowledge> relevantKnowledge = List.of();
            if (knowledgeBaseClient.isAvailable()) {
                relevantKnowledge = knowledgeBaseClient.searchKnowledge(searchQuery, 3);
                logger.info("Retrieved {} relevant knowledge entries for summary", relevantKnowledge.size());
            }

            String systemPrompt = buildSystemPrompt(profile, true, relevantKnowledge);
            String conversationContext = buildConversationContext(session);

            String summaryJson;
            if ("gemini".equalsIgnoreCase(provider)) {
                summaryJson = callGeminiAPI(systemPrompt, conversationContext, true);
            } else {
                summaryJson = callOpenAIAPI(systemPrompt, conversationContext, true);
            }

            return parseSummaryFromJson(summaryJson, session, profile);

        } catch (Exception e) {
            logger.error("Error generating anamnesis summary", e);
            return createFallbackSummary(session, profile);
        }
    }

    @Override
    public boolean isAvailable() {
        boolean available = false;

        if ("gemini".equalsIgnoreCase(provider)) {
            available = geminiEnabled && geminiApiKey != null && !geminiApiKey.isBlank();
            logger.debug("Gemini service available: {}", available);
        } else {
            available = openaiEnabled && openaiApiKey != null && !openaiApiKey.isBlank();
            logger.debug("OpenAI service available: {}", available);
        }

        return available;
    }

    private String callGeminiAPI(String systemPrompt, String conversationContext, boolean forSummary) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

            String fullPrompt = systemPrompt + "\n\n" + conversationContext;
            if (forSummary) {
                fullPrompt += "\n\nGenera SOLO un JSON válido con el siguiente formato:\n" +
                    "{\n" +
                    "  \"chiefComplaint\": \"motivo principal\",\n" +
                    "  \"historyOfPresentIllness\": \"descripción detallada\",\n" +
                    "  \"pastMedicalHistory\": \"antecedentes\",\n" +
                    "  \"medications\": [\"medicamento1\", \"medicamento2\"],\n" +
                    "  \"allergies\": [\"alergia1\"],\n" +
                    "  \"redFlags\": [\"señal de alarma\"],\n" +
                    "  \"additionalNotes\": \"notas adicionales\"\n" +
                    "}\n\nResponde SOLO con el JSON, sin texto adicional.";
            }

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", fullPrompt);
            parts.add(part);
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", temperature);
            generationConfig.put("maxOutputTokens", maxTokens);
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            logger.debug("Calling Gemini API: {}", url);
            String response = restTemplate.postForObject(url, request, String.class);

            JsonNode jsonNode = objectMapper.readTree(response);
            String text = jsonNode.at("/candidates/0/content/parts/0/text").asText();

            logger.debug("Gemini response received: {} characters", text.length());
            return text;

        } catch (Exception e) {
            logger.error("Error calling Gemini API", e);
            throw new RuntimeException("Error calling Gemini API", e);
        }
    }

    private String callOpenAIAPI(String systemPrompt, String conversationContext, boolean forSummary) {
        logger.warn("OpenAI API integration not fully implemented. Using fallback response.");

        if (forSummary) {
            return "{\n" +
                "  \"chiefComplaint\": \"Síntomas reportados por el paciente\",\n" +
                "  \"historyOfPresentIllness\": \"Información recopilada durante la conversación\",\n" +
                "  \"pastMedicalHistory\": \"Ver perfil del paciente\",\n" +
                "  \"medications\": [],\n" +
                "  \"allergies\": [],\n" +
                "  \"redFlags\": [],\n" +
                "  \"additionalNotes\": \"Resumen generado con OpenAI (implementación pendiente)\"\n" +
                "}";
        }

        return "Entiendo sus síntomas. ¿Podría proporcionar más detalles sobre la duración e intensidad?";
    }

    private String buildSystemPrompt(ProfileSnapshot profile, boolean forSummary, List<MedicalKnowledge> relevantKnowledge) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Eres un asistente médico virtual especializado en realizar anamnesis médicas ");
        prompt.append("para pacientes en zonas rurales de Perú (como Cajamarca). ");
        prompt.append("Tu objetivo es recopilar información médica relevante de manera empática, clara y profesional. ");

        if (!forSummary) {
            prompt.append("Haz preguntas abiertas y específicas sobre síntomas, duración, intensidad y factores relacionados. ");
            prompt.append("Sé empático y usa un lenguaje sencillo. ");
        }

        prompt.append("Identifica posibles señales de alarma (red flags) que requieran atención urgente.\n\n");

        // Add Ayllucare Knowledge Base context (RAG)
        if (!relevantKnowledge.isEmpty()) {
            prompt.append("===== CONOCIMIENTO MÉDICO DE AYLLUCARE =====\n");
            prompt.append("Utiliza la siguiente información del dataset propio de AylluCare como referencia:\n\n");

            for (MedicalKnowledge knowledge : relevantKnowledge) {
                prompt.append("► ").append(knowledge.topic()).append(":\n");
                prompt.append("  Descripción: ").append(knowledge.description()).append("\n");

                if (!knowledge.symptoms().isEmpty()) {
                    prompt.append("  Síntomas comunes: ").append(String.join(", ", knowledge.symptoms())).append("\n");
                }

                if (!knowledge.recommendations().isEmpty()) {
                    prompt.append("  Recomendaciones: ").append(String.join("; ", knowledge.recommendations())).append("\n");
                }

                if (!knowledge.redFlags().isEmpty()) {
                    prompt.append("  ⚠️ SEÑALES DE ALARMA: ").append(String.join(", ", knowledge.redFlags())).append("\n");
                }

                prompt.append("\n");
            }
            prompt.append("===== FIN DEL CONOCIMIENTO DE AYLLUCARE =====\n\n");

            prompt.append("IMPORTANTE: Basa tus preguntas y evaluación en el conocimiento médico de AylluCare proporcionado arriba. ");
            prompt.append("Presta especial atención a las señales de alarma mencionadas.\n\n");
        }

        if (profile != null && profile.hasConsentForAI()) {
            prompt.append("INFORMACIÓN DEL PACIENTE:\n");
            prompt.append(profile.getFormattedSummary());
            prompt.append("\n");
        }

        if (forSummary) {
            prompt.append("\nTu tarea es analizar toda la conversación y generar un resumen médico estructurado.\n");
            prompt.append("Identifica: motivo de consulta, historia de enfermedad actual, antecedentes relevantes, ");
            prompt.append("medicamentos, alergias y ESPECIALMENTE señales de alarma (red flags) que indiquen urgencia.\n");
        } else {
            prompt.append("\nResponde en español de manera concisa (máximo 2-3 líneas). ");
            prompt.append("NO des diagnósticos ni recomendaciones de tratamiento. Solo recopila información.\n");
        }

        return prompt.toString();
    }

    /**
     * Extracts search query from session to query knowledge base.
     * Uses initial reason and latest patient messages.
     */
    private String extractSearchQuery(AnamnesisSession session) {
        StringBuilder query = new StringBuilder();

        // Add initial reason
        if (session.getInitialReason() != null && !session.getInitialReason().isBlank()) {
            query.append(session.getInitialReason()).append(" ");
        }

        // Add latest patient messages (last 3)
        List<ConversationMessage> messages = session.getMessages();
        int startIndex = Math.max(0, messages.size() - 3);

        for (int i = startIndex; i < messages.size(); i++) {
            ConversationMessage message = messages.get(i);
            if (message.getSenderType() == SenderType.PATIENT) {
                query.append(message.getContent()).append(" ");
            }
        }

        return query.toString().trim();
    }

    private String buildConversationContext(AnamnesisSession session) {
        StringBuilder context = new StringBuilder();
        context.append("CONVERSACIÓN:\n\n");

        if (session.getInitialReason() != null && !session.getInitialReason().isBlank()) {
            context.append("Motivo inicial: ").append(session.getInitialReason()).append("\n\n");
        }

        for (ConversationMessage message : session.getMessages()) {
            if (message.getSenderType() != SenderType.SYSTEM) {
                String sender = message.getSenderType() == SenderType.PATIENT ? "Paciente" : "Asistente";
                context.append(sender).append(": ").append(message.getContent()).append("\n");
            }
        }

        return context.toString();
    }

    private AnamnesisSummary parseSummaryFromJson(String jsonText, AnamnesisSession session, ProfileSnapshot profile) {
        try {
            jsonText = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode jsonNode = objectMapper.readTree(jsonText);

            String chiefComplaint = jsonNode.has("chiefComplaint") ?
                jsonNode.get("chiefComplaint").asText() : session.getInitialReason();
            String historyOfPresentIllness = jsonNode.has("historyOfPresentIllness") ?
                jsonNode.get("historyOfPresentIllness").asText() : "";
            String pastMedicalHistory = jsonNode.has("pastMedicalHistory") ?
                jsonNode.get("pastMedicalHistory").asText() : "";
            String additionalNotes = jsonNode.has("additionalNotes") ?
                jsonNode.get("additionalNotes").asText() : "";

            List<String> medications = extractStringList(jsonNode, "medications");
            List<String> allergies = extractStringList(jsonNode, "allergies");
            List<String> redFlags = extractStringList(jsonNode, "redFlags");

            if (profile != null) {
                if (profile.getAllergies() != null && !profile.getAllergies().isEmpty()) {
                    for (String allergy : profile.getAllergies()) {
                        if (!allergies.contains(allergy)) {
                            allergies.add(allergy);
                        }
                    }
                }
                if (profile.getCurrentMedications() != null && !profile.getCurrentMedications().isEmpty()) {
                    for (String med : profile.getCurrentMedications()) {
                        if (!medications.contains(med)) {
                            medications.add(med);
                        }
                    }
                }
            }

            return new AnamnesisSummary(
                chiefComplaint,
                historyOfPresentIllness,
                pastMedicalHistory,
                medications,
                allergies,
                redFlags,
                additionalNotes
            );

        } catch (Exception e) {
            logger.error("Error parsing summary JSON: {}", jsonText, e);
            return createFallbackSummary(session, profile);
        }
    }

    private List<String> extractStringList(JsonNode jsonNode, String fieldName) {
        List<String> result = new ArrayList<>();
        if (jsonNode.has(fieldName) && jsonNode.get(fieldName).isArray()) {
            jsonNode.get(fieldName).forEach(item -> {
                if (!item.asText().isBlank()) {
                    result.add(item.asText());
                }
            });
        }
        return result;
    }

    private AnamnesisSummary createFallbackSummary(AnamnesisSession session, ProfileSnapshot profile) {
        List<String> medications = new ArrayList<>();
        List<String> allergies = new ArrayList<>();

        if (profile != null) {
            if (profile.getAllergies() != null) {
                allergies.addAll(profile.getAllergies());
            }
            if (profile.getCurrentMedications() != null) {
                medications.addAll(profile.getCurrentMedications());
            }
        }

        return new AnamnesisSummary(
            session.getInitialReason() != null ? session.getInitialReason() : "No especificado",
            "Información recopilada durante la conversación (resumen automático generado por falta de servicio LLM)",
            profile != null && profile.getChronicConditions() != null ?
                String.join(", ", profile.getChronicConditions()) : "Sin antecedentes relevantes",
            medications,
            allergies,
            new ArrayList<>(),
            "Resumen generado automáticamente. Requiere revisión médica."
        );
    }
}

