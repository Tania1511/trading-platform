package com.tradingplatform.surveillance_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "surveillance.agent")
@Component
public class AgentProperties {

//    private String anthropicApiKey;

    private String model = "claude-sonnet-5";

    private String anthropicBaseUrl = /*"https://api-anthropic.com"*/ "http://host.minikube.internal:11434";

    private String orderGatewayUrl = "http://order-gateway:8081";
    private String positionServiceUrl = "https://position-service:8081";

    private String getKeyCloakTokenUrl = "http://keycloak:8080/realms/trading-platform/protocol/openid-connect/token";
    private String clientId = "surveillance-service";
    private String clientSecret;

//    public String getAnthropicApiKey() {
//        return anthropicApiKey;
//    }
//
//    public void setAnthropicApiKey(String anthropicApiKey) {
//        this.anthropicApiKey = anthropicApiKey;
//    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAnthropicBaseUrl() {
        return anthropicBaseUrl;
    }

    public void setAnthropicBaseUrl(String anthropicBaseUrl) {
        this.anthropicBaseUrl = anthropicBaseUrl;
    }

    public String getOrderGatewayUrl() {
        return orderGatewayUrl;
    }

    public void setOrderGatewayUrl(String orderGatewayUrl) {
        this.orderGatewayUrl = orderGatewayUrl;
    }

    public String getPositionServiceUrl() {
        return positionServiceUrl;
    }

    public void setPositionServiceUrl(String positionServiceUrl) {
        this.positionServiceUrl = positionServiceUrl;
    }

    public String getGetKeyCloakTokenUrl() {
        return getKeyCloakTokenUrl;
    }

    public void setGetKeyCloakTokenUrl(String getKeyCloakTokenUrl) {
        this.getKeyCloakTokenUrl = getKeyCloakTokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
