package com.programming.tsveti.youtubeclone.controller;

import com.programming.tsveti.youtubeclone.dto.UploadVideoResponse;
import com.programming.tsveti.youtubeclone.dto.VideoDto;
import com.programming.tsveti.youtubeclone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse uploadVideo(@RequestParam("file")MultipartFile file) {
      return videoService.uploadVideo(file);
    }

    @PostMapping("/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam("videoId") String videoId) {
        System.out.println("ei go " + videoId);
        return videoService.uploadThumbnail(file, videoId);
    }
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public VideoDto editVideoMetaData(@RequestBody VideoDto videoDto){
        return videoService.editVideo(videoDto);
    }
}
