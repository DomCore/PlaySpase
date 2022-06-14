package com.example.templates.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.templates.model.Category;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private CategoryRepository categoryRepository;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category findByName(String name) {
    return categoryRepository.findByName(name);
  }
  public Category findById(Integer id) {
    return categoryRepository.findById(id).get();
  }

  public void deleteCategoryById(Integer g) {
    categoryRepository.deleteCategoryById(g);
  }

  public List<Category> findByGameId(Integer Id) {
    return categoryRepository.findByGameId(Id);
  }
  public void saveCategory(Category category) {
    categoryRepository.save(category);
  }

  public List<Category> getAllCategories() {
    return categoryRepository.getAll();
  }

  public List<StringAndListWrapper> createTemplates(Integer id) {
    Category category = findById(id);
    String[] temp = category.getTemplates().split(",");
    int subTemp = 0;
    List<StringAndListWrapper> templates = new ArrayList<>();
    for (int i = 0; i < temp.length; i++) {
      if (!temp[i].equals("SubTemplate")) {
        templates.add(new StringAndListWrapper(temp[i]));
      } else {
        if (templates.get(templates.size() - 1).getArrString() != null) {
          templates.get(templates.size() - 1)
              .setArrString(Stream.concat(templates.get(templates.size() - 1).getArrString().stream(), Stream.of(category.getSubTemplates()
                      .get(subTemp)))
                  .collect(Collectors.toList()));
          subTemp++;
        } else {
          templates.get(templates.size() - 1).setArrString(List.of(category.getSubTemplates().get(subTemp)));
          subTemp++;
        }
      }
    }
    return templates;
  }
}
