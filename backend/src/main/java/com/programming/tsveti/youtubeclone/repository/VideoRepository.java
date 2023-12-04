package com.programming.tsveti.youtubeclone.repository;

import com.programming.tsveti.youtubeclone.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String>{

}
