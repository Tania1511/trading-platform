package com.tradingplatform.surveillance_service.agent;

import tools.jackson.databind.JsonNode;

import java.util.Map;

public interface AgentTool {

    String name();

    String description();

    Map<String, Object> inputSchema();

    String execute(JsonNode input);
}
