package com.example.templates.repository;

import com.example.templates.model.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {
}