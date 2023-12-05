package com.programming.tsveti.youtubeclone.service;

import com.programming.tsveti.youtubeclone.model.Video;
import com.programming.tsveti.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    public void uploadVideo(MultipartFile multipartFile) {
        // Upload video to AWS S3
        // Save Video Data to Database

        String videoUrl = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        videoRepository.save(video);
    }
}
