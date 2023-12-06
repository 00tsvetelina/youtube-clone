package com.programming.tsveti.youtubeclone.service;
import com.programming.tsveti.youtubeclone.dto.UploadVideoResponse;
import com.programming.tsveti.youtubeclone.dto.VideoDto;
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
    public UploadVideoResponse uploadVideo(MultipartFile multipartFile) {
        // Upload video to AWS S3
        // Save Video Data to Database

        String videoUrl = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        var savedVideo =  videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }

    public VideoDto editVideo(VideoDto videoDto) {
        // Find the video by videoId
        var savedVideo = getVideoById(videoDto.getId());

        // Map the videoDto fields to video
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setTumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        // Save the video to the database
        videoRepository.save(savedVideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        // Find video by video i
        var savedVideo = getVideoById(videoId);

        String thumbnailUrl = s3Service.uploadFile(file);

        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }

    Video getVideoById(String videoId){
        return videoRepository.findById(videoId)
                .orElseThrow(()-> new IllegalArgumentException("Cannot find video id - " + videoId));
    }
}
