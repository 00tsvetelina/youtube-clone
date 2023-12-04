package com.programming.tsveti.youtubeclone.config;

import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonClientConfig {
    @Bean
    public AmazonS3Client amazonS3Client() {
        return new AmazonS3Client();
    }
}
