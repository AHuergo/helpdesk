package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.Article;
import com.anahuergo.helpdesk.domain.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByStatus(ArticleStatus status);
    List<Article> findByCategory(String category);
    List<Article> findByTitleContainingIgnoreCase(String keyword);

}