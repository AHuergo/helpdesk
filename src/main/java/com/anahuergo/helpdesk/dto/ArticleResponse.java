package com.anahuergo.helpdesk.dto;

import com.anahuergo.helpdesk.domain.Article;
import com.anahuergo.helpdesk.domain.ArticleStatus;
import java.time.LocalDateTime;

public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String tags;
    private ArticleStatus status;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.category = article.getCategory();
        this.tags = article.getTags();
        this.status = article.getStatus();
        this.authorName = article.getAuthor() != null ? article.getAuthor().getName() : null;
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getTags() { return tags; }
    public ArticleStatus getStatus() { return status; }
    public String getAuthorName() { return authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

}