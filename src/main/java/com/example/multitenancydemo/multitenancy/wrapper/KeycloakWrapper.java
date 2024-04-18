package com.example.multitenancydemo.multitenancy.wrapper;

import com.example.multitenancydemo.multitenancy.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Collections;
import java.util.List;

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

    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(clientIdAdmin)
                .grantType("password")
                .username(realmCreatorUsername)
                .password(realmCreatorPassword)
                .build();
    }

    public RealmResource realmResource(String realm) { return keycloak().realm(realm); }

    public UsersResource usersResource(String realm) { return realmResource(realm).users(); }

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
        try {
            keycloak().realms().create(getRealmRepresentation(realmName));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't create a new realm with name=" + realmName);
        }
    }

    private RealmRepresentation getRealmRepresentation(String realmName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Load the JSON file from the classpath
            Resource resource = resourceLoader.getResource("classpath:realm-blueprint.json");
            RealmRepresentation realmRepresentation = objectMapper.readValue(resource.getInputStream(), RealmRepresentation.class);
            realmRepresentation.setRealm(realmName);
            return realmRepresentation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't load realm blueprint");
        }
    }

    public void createUser(String realmName,String username, String password, String role) {
        var newUser = prepareUserRepresentation(username,password);
        try (Response response = usersResource(realmName).create(newUser)){
            String userId = CreatedResponseUtil.getCreatedId(response);
            updateUserRoles(realmName, role, userId);
        }
    }

    public void updateUserRoles(String realmName, String role, String userId) {
        if (!Utils.isNullOrEmpty(role)) {
            // Get realm role
            var roleRepresentation = realmResource(realmName).roles().get(role).toRepresentation();

            // Set user realm role
            usersResource(realmName).get(userId).roles().realmLevel().add(List.of(roleRepresentation));
        }
    }

    public UserRepresentation prepareUserRepresentation(String username, String password) {
        var kcUser = new UserRepresentation();
        kcUser.setUsername(username);
        kcUser.setEnabled(true);
        var credentialRepresentation = createPasswordCredentials(password);
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        return kcUser;
    }

    public CredentialRepresentation createPasswordCredentials(String password) {
        var passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

//    private ObjectNode getRealmProperties(String realmName) {
//        try {
//            // Load the JSON file from the classpath
//            Resource resource = resourceLoader.getResource("classpath:realm-blueprint.json");
//
//            // Parse JSON using ObjectMapper
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
//            return ((ObjectNode) rootNode).put("realm", realmName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Can't load realm blueprint");
//        }
//    }

//    public void createRealm(String realmName) {
//        var tokenResponse = accessTokenResponseAdmin();
//        var headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + tokenResponse.getToken());
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        String url = serverUrl + "/admin/realms";
//        var entity = new HttpEntity<>(getRealmProperties(realmName), headers);
//        var response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
//        if (!response.getStatusCode().equals(HttpStatus.CREATED))
//            throw new RuntimeException("Can't create a new realm with name=" + realmName);
//    }

    //    public void createUser(String realmName,String username, String password, List<String> roles) {
//        var tokenResponse = accessTokenResponseAdmin();
//        var headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + tokenResponse.getToken());
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        String url = serverUrl + "/admin/realms/"+realmName+"/users";
//        var entity = new HttpEntity<>(getUserProperties(username,password,roles), headers);
//        var response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
//        if (!response.getStatusCode().equals(HttpStatus.CREATED))
//            throw new RuntimeException("Can't create user=" + username + " in realm=" + realmName);
//    }


//    private ObjectNode getUserProperties(String username, String password, List<String> roles) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode userData = objectMapper.createObjectNode();
//        userData.put("username", username);
//        userData.put("enabled", true);
//
//        ObjectNode credentialsNode = objectMapper.createObjectNode();
//        credentialsNode.put("type", "password");
//        credentialsNode.put("value", password);
//        credentialsNode.put("temporary", false);
//
//        userData.set("credentials", objectMapper.createArrayNode().add(credentialsNode));
//        return userData;
//    }
}
