package com.programming.tsveti.youtubeclone.service;
import com.programming.tsveti.youtubeclone.dto.CommentDto;
import com.programming.tsveti.youtubeclone.dto.UploadVideoResponse;
import com.programming.tsveti.youtubeclone.dto.VideoDto;
import com.programming.tsveti.youtubeclone.model.Comment;
import com.programming.tsveti.youtubeclone.model.Video;
import com.programming.tsveti.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;
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
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        // Save the video to the database
        videoRepository.save(savedVideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        // Find video by video i
        var savedVideo = getVideoById(videoId);

        String thumbnailUrl = s3Service.uploadFile(file);
        savedVideo.setThumbnailUrl(thumbnailUrl);
        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }

    Video getVideoById(String videoId){
        return videoRepository.findById(videoId)
                .orElseThrow(()-> new IllegalArgumentException("Cannot find video id - " + videoId));
    }

    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        userService.addVideosToHistory(videoId);

        return mapToVideo(savedVideo);

    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.increaseViewCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {
        // Get video by id
        Video videoById = getVideoById(videoId);

        // If like
        // Increment like count
        // like - 0, dislike - 0
        // like - 1, dislike - 0
        // like - 0, dislike - 0

        // like - 0, dislike - 1
        // like - 1, dislike - 0


        // If dislike
        //  increment dislike count and decrement dislike count


        if (userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        } else if (userService.ifDislikedVideo(videoId)) {
            videoById.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        } else {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideo(videoById);

    }

    public VideoDto dislikeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        // If like
        // Increment like count
        // like - 0, dislike - 0
        // like - 1, dislike - 0
        // like - 0, dislike - 0

        // like - 0, dislike - 1
        // like - 1, dislike - 0


        // If dislike
        //  increment dislike count and decrement dislike count


        if (userService.ifDislikedVideo(videoId)) {
            videoById.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDislikes();
            userService.addToDislikedVideos(videoId);
        } else {
            videoById.incrementLikes();
            userService.addToDislikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideo(videoById);

    }

    private VideoDto mapToVideo(Video videoById) {
        VideoDto videoDto = new VideoDto();

        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getId());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDislikes().get());

        videoDto.setViewCount(videoById.getViewCount().get());

        return videoDto;
    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(commentDto.getAuthorId());
        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
       Video video = getVideoById(videoId);
       List<Comment> commentList = video.getCommentList();

      return commentList.stream()
               .map(this::mapToCommentDto)
               .toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());

        return commentDto;
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll()
                .stream().map(this::mapToVideo).toList();
    }
}
