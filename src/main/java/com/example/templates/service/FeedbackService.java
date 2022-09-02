package com.example.templates.service;

import java.util.List;

import com.example.templates.model.Feedback;
import com.example.templates.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

  private FeedbackRepository feedbackRepository;

  @Autowired
  public FeedbackService(FeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public List<Feedback> findByTargetId(Integer id) {
    return feedbackRepository.findByTargetId(id);
  }
  public Feedback findByLotId(Integer id) {
    return feedbackRepository.findByLotId(id);
  }
  public void saveFeedback(Feedback feedback) {
    feedbackRepository.save(feedback);
  }
}
