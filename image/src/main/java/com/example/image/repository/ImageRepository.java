package com.example.image.repository;

import com.example.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ImageRepository extends JpaRepository<Image, String> {
    Image findByFileName(String fileName);

    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.fileName IN ?1")
    void deleteAllByFileName(String[] fileNames);

    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.fileName=:fileName")
    void deleteByFileName(String fileName);
}