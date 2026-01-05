package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Article;
import com.anahuergo.helpdesk.domain.ArticleStatus;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.dto.ArticleResponse;
import com.anahuergo.helpdesk.repository.ArticleRepository;
import com.anahuergo.helpdesk.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ArticleResponse> findAll() {
        return articleRepository.findAll().stream()
                .map(ArticleResponse::new)
                .toList();
    }

    @GetMapping("/published")
    public List<ArticleResponse> findPublished() {
        return articleRepository.findByStatus(ArticleStatus.PUBLISHED).stream()
                .map(ArticleResponse::new)
                .toList();
    }

    @GetMapping("/search")
    public List<ArticleResponse> search(@RequestParam String keyword) {
        return articleRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(ArticleResponse::new)
                .toList();
    }

    @GetMapping("/category/{category}")
    public List<ArticleResponse> findByCategory(@PathVariable String category) {
        return articleRepository.findByCategory(category).stream()
                .map(ArticleResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ArticleResponse findById(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElseThrow();
        return new ArticleResponse(article);
    }

    @PostMapping
    public ArticleResponse create(@RequestBody Article article, @RequestParam Long authorId) {
        User author = userRepository.findById(authorId).orElseThrow();
        article.setAuthor(author);
        Article saved = articleRepository.save(article);
        return new ArticleResponse(saved);
    }

    @PutMapping("/{id}/publish")
    public ArticleResponse publish(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElseThrow();
        article.setStatus(ArticleStatus.PUBLISHED);
        Article saved = articleRepository.save(article);
        return new ArticleResponse(saved);
    }

    @PutMapping("/{id}/archive")
    public ArticleResponse archive(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElseThrow();
        article.setStatus(ArticleStatus.ARCHIVED);
        Article saved = articleRepository.save(article);
        return new ArticleResponse(saved);
    }

}