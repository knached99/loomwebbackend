package com.example.loomweb.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.loomweb.model.User;
import com.example.loomweb.model.Video;
import com.example.loomweb.repository.UserRepository;
import com.example.loomweb.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    private User getDefaultUser() {
        return userRepository.findByEmail("default@user.com").orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail("default@user.com");
            newUser.setPassword("password");
            return userRepository.save(newUser);
        });
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("video") MultipartFile file,
                                    @RequestParam("title") String title,
                                    @RequestParam(value = "description", required = false) String description) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + File.separator + filename;
        file.transferTo(new File(filePath));
        String url = "/uploads/" + filename;

        User user = getDefaultUser();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Video video = new Video(null, url, title, description, now, false, user);
        videoRepository.save(video);

        return ResponseEntity.ok(video);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateVideo(@PathVariable Long id,
                                         @RequestBody VideoUpdateRequest updateRequest) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        User user = getDefaultUser();

        if (!video.getUser().getEmail().equals(user.getEmail())) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", "You do not own this video"));
        }

        if (updateRequest.getTitle() != null) {
            video.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getDescription() != null) {
            video.setDescription(updateRequest.getDescription());
        }

        videoRepository.save(video);
        return ResponseEntity.ok(video);
    }

    // DTO for update request
    public static class VideoUpdateRequest {
        private String title;
        private String description;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    @GetMapping("/me")
    public List<Video> getMyVideos() {
        User user = getDefaultUser();
        return videoRepository.findByUser(user);
    }

    @GetMapping("/favorites")
    public List<Video> getFavorites() {
        User user = getDefaultUser();
        return videoRepository.findByUserAndFavoriteTrue(user);
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        User user = getDefaultUser();

        if (!video.getUser().getEmail().equals(user.getEmail())) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", "You do not own this video"));
        }

        video.setFavorite(!video.isFavorite());
        videoRepository.save(video);
        return ResponseEntity.ok(video);
    }
}
