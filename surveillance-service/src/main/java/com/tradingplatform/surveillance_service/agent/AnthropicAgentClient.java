package com.tradingplatform.surveillance_service.agent;

import com.tradingplatform.surveillance_service.config.AgentProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class AnthropicAgentClient {

    private static final int MAX_ITERATIONS = 5;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AgentProperties agentProperties;
    private final List<AgentTool> tools;


    public AnthropicAgentClient(AgentProperties agentProperties, List<AgentTool> tools) {
        this.agentProperties = agentProperties;
        this.tools = tools;
    }

    public String investigate(String initialPrompt) {
        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(userTextMessage(initialPrompt));

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            ObjectNode response = sendRequest(messages);

            String stopReason  = response.get("stop_reason").asString();
            ArrayNode content = (ArrayNode) response.get("content");

            if(! "tool_use".equals(stopReason)){
                return extractText(content);
            }

            ObjectNode assistantTurn = objectMapper.createObjectNode();
            assistantTurn.put("role", "assistant");
            assistantTurn.set("content", content);
            messages.add(assistantTurn);

            ArrayNode toolResults = objectMapper.createArrayNode();
            for (JsonNode block : content){
                if("tool_use".equals(block.get("type").asString())){
                    String toolResult = executeTool(block);
                    ObjectNode resultBlock = objectMapper.createObjectNode();
                    resultBlock.put("type","tool_result");
                    resultBlock.put("tool_use_id", block.get("id").asString());
                    resultBlock.put("content",toolResult);
                    toolResults.add(resultBlock);
                }
            }

            ObjectNode toolResultMessage = objectMapper.createObjectNode();
            toolResultMessage.put("role","user");
            toolResultMessage.set("content",toolResults);
            messages.add(toolResultMessage);
        }

        return "Investigation exceeded maximum reasoning iterations without a final answer.";
    }

    private String executeTool(JsonNode toolUseBlock){
        String toolName = toolUseBlock.get("name").asString();
        JsonNode input = toolUseBlock.get("input");

        return tools.stream()
                .filter(t -> t.name().equals(toolName))
                .findFirst()
                .map(t -> t.execute(input))
                .orElse("Unknown tool requested: " + toolName);
    }

    public ObjectNode sendRequest(ArrayNode messages){
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model",agentProperties.getModel());
        requestBody.put("max_tokens", 1024);
        requestBody.set("messages", messages);
        requestBody.set("tools", buildToolDefinitions());

        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(agentProperties.getAnthropicBaseUrl() + "/v1/messages"))
//                    .header("x-api-key", agentProperties.getAnthropicApiKey())
//                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();


            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return (ObjectNode) objectMapper.readTree(response.body());
        }catch (Exception e){
            throw new RuntimeException("Anthropic Api call failed",e);
        }
    }

    private ArrayNode buildToolDefinitions(){
        ArrayNode toolsArray = objectMapper.createArrayNode();
        for(AgentTool tool : tools){
            ObjectNode toolDef = objectMapper.createObjectNode();
            toolDef.put("name",tool.name());
            toolDef.put("description", tool.description());
            toolDef.set("input_schema", objectMapper.valueToTree(tool.inputSchema()));
            toolsArray.add(toolDef);
        }
        return toolsArray;
    }

    private ObjectNode userTextMessage(String text){
        ObjectNode message = objectMapper.createObjectNode();
        message.put("role","user");
        message.put("content",text);
        return message;
    }

    private String extractText(ArrayNode content){
        StringBuilder sb = new StringBuilder();
        for(JsonNode block : content) {
            if("text".equals(block.get("type").asString())){
                sb.append(block.get("text").asString());
            }
        }
        return sb.toString();
    }
}
