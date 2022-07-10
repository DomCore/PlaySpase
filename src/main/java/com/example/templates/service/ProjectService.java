package com.example.templates.service;

import com.example.templates.model.Project;
import com.example.templates.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

  @Autowired
  private ProjectRepository projectRepository;

  public void saveProject(Project project) {
    projectRepository.save(project);
  }

  public Project findById(int id) {
    return  projectRepository.findById(id).get();
  }

  public double getCoef() {
    return 1 - Double.valueOf(findById(1).getTax())/100;
  }

}
