package com.programming.tsveti.youtubeclone.service;

import com.programming.tsveti.youtubeclone.model.User;
import com.programming.tsveti.youtubeclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
       String sub = ((Jwt)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getClaim("sub");

       return userRepository.findBySub(sub)
               .orElseThrow(()-> new IllegalArgumentException("Cannot find user with sub - " + sub));
    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public boolean ifLikedVideo(String videoId){
      return getCurrentUser().getLikedVideos()
                .stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public boolean ifDislikedVideo(String videoId){
        return getCurrentUser().getDislikedVideos()
                .stream().anyMatch(dislikedVideo -> dislikedVideo.equals(videoId));
    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void removeFromDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addToDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addVideosToHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoId);
        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId) {
        // retrieve current user
        User currentUser = getCurrentUser();

        // add user id to subscribed user set
        currentUser.addToSubscribedToUser(userId);

        // retrieve target user and add to subscribers set
        User user = getUserById(userId);
        user.addToSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }



    public void unsubscribeUser(String userId) {
        // retrieve current user
        User currentUser = getCurrentUser();

        // add user id to subscribed user set
        currentUser.removeFromSubscribedToUser(userId);

        // retrieve target user and add to subscribers set
        User user = getUserById(userId);
        user.removeFromSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public Set<String> userHistory(String userId) {
        User user = getUserById(userId);
        return user.getVideoHistory();
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("Cannot find user id - " + userId));
    }
}

