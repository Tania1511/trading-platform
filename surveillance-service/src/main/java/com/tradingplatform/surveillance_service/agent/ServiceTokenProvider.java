package com.tradingplatform.surveillance_service.agent;

import com.tradingplatform.surveillance_service.config.AgentProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

@Component
public class ServiceTokenProvider {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AgentProperties agentProperties;


    private static final long EXPIRY_BUFFER_SECONDS = 30;

    private String cachedToken;
    private Instant cachedTokenExpiresAt = Instant.EPOCH;

    public ServiceTokenProvider(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    public synchronized String getAccessToken() {
        if(cachedToken !=null && Instant.now().isBefore(cachedTokenExpiresAt)){
            return cachedToken;
        }

        try{

            String form = "grant_type=client_credentials"
                    + "&client_id=" + agentProperties.getClientId()
                    + "&client_secret=" + agentProperties.getClientSecret();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(agentProperties.getKeycloakTokenUrl()))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());

            cachedToken = body.get("access_token").asString();
            long expiredInSeconds = body.get("expires_in").asLong();
            cachedTokenExpiresAt = Instant.now().plusSeconds(expiredInSeconds - EXPIRY_BUFFER_SECONDS);

            return cachedToken;
        } catch (Exception e){
            throw new RuntimeException("Failed to obtain service token from keycloak", e);
        }
    }
}
