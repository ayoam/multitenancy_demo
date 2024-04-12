package com.example.multitenancydemo.multitenancy.wrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class KeycloakWrapper {
    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;


    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak-admin-cli}")
    private String clientIdAdmin;

    @Value("${keycloak-realm-creator-username}")
    private String realmCreatorUsername;

    @Value("${keycloak-realm-creator-password}")
    private String realmCreatorPassword;


    public AccessTokenResponse accessTokenResponseAdmin() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", clientIdAdmin);
        requestParams.add("grant_type", "password");
        requestParams.add("username", realmCreatorUsername);
        requestParams.add("password", realmCreatorPassword);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        String url = serverUrl + "realms/master/protocol/openid-connect/token";
        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(url, request, AccessTokenResponse.class);
        return response.getBody();
    }

    public void createRealm(String realmName) {
        var tokenResponse = accessTokenResponseAdmin();
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenResponse.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = serverUrl + "/admin/realms";
        var entity = new HttpEntity<>(getRealmProperties(realmName), headers);
        var response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
        if (!response.getStatusCode().equals(HttpStatus.CREATED))
            throw new RuntimeException("Can't create a new realm with name=" + realmName);
    }

    private ObjectNode getRealmProperties(String realmName) {
        try {
            // Load the JSON file from the classpath
            Resource resource = resourceLoader.getResource("classpath:realm-blueprint.json");

            // Parse JSON using ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            return ((ObjectNode) rootNode).put("realm", realmName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't load realm blueprint");
        }
    }
}
