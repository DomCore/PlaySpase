package com.example.templates.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    exposeDirectory("game_logos/", registry);
    exposeDirectory("user_logos/", registry);
    registry.addResourceHandler("game_logos").addResourceLocations("59/2.jpg");
    registry.addResourceHandler("game_logos").addResourceLocations("/59/2.jpg");
  }

  private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
    Path uploadDir = Paths.get(dirName);
    String uploadPath = uploadDir.toFile().getAbsolutePath();

    if (dirName.startsWith("../")) dirName = dirName.replace("../", "");

    registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    System.out.println("/" + dirName + "/**");
    System.out.println("file:/"+ uploadPath + "/");
  }
}