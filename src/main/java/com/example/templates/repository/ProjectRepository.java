package com.example.templates.repository;

import com.example.templates.model.Project;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

}