package com.example.loomweb.repository;

import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loomweb.model.User; 
import com.example.loomweb.model.Video; 


public interface VideoRepository extends JpaRepository<Video, Long>{
    List<Video> findByUser(User user);
    List<Video> findByUserAndFavoriteTrue(User user);
}
