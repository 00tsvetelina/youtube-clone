package com.programming.tsveti.youtubeclone.service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.tsveti.youtubeclone.dto.UserInfoDto;
import com.programming.tsveti.youtubeclone.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.programming.tsveti.youtubeclone.repository.UserRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    @Value("${auth0.endpoint}")
    private String userInfoEndpoint;
    private final UserRepository userRepository;
    public String registerUser(String tokenValue){
        // Make a call to the userInfo Endpoint;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(userInfoEndpoint))
                .setHeader("Authorization", String.format("Bearer %s", tokenValue))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        try {
        HttpResponse<String> responseString =httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String body = responseString.body();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            UserInfoDto userInfoDto = objectMapper.readValue(body, UserInfoDto.class);

            Optional<User> userBySubject = userRepository.findBySub(userInfoDto.getSub());
            if(userBySubject.isPresent()) {
                return userBySubject.get().getId();
            } else {
                User user = new User();
                user.setFirstName(userInfoDto.getGivenName());
                user.setLastName(userInfoDto.getFamilyName());
                user.setFullName(userInfoDto.getName());
                user.setEmailAddress(userInfoDto.getEmail());
                user.setSub(userInfoDto.getSub());

                return userRepository.save(user).getId();
            }

        } catch (Exception exception) {
            throw new RuntimeException("Exception occurred while registering user", exception);
        }
        // Fetch user details and save them to the database;
    }

}
