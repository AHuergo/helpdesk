package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Article;
import com.anahuergo.helpdesk.domain.ArticleStatus;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ArticleControllerTest {

    @Autowired
    private ArticleController articleController;

    @Autowired
    private UserRepository userRepository;

    private User author;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setEmail("author@test.com");
        author.setPassword("12345678");
        author.setName("Author");
        author.setSurname("Test");
        author = userRepository.save(author);
    }

    @Test
    void shouldCreateArticle() {
        Article article = new Article();
        article.setTitle("Como resetear password");
        article.setContent("Instrucciones para resetear...");
        article.setCategory("Seguridad");

        var response = articleController.create(article, author.getId());

        assertNotNull(response);
        assertEquals("Como resetear password", response.getTitle());
        assertEquals(ArticleStatus.DRAFT, response.getStatus());
    }

    @Test
    void shouldPublishArticle() {
        Article article = new Article();
        article.setTitle("Articulo para publicar");
        article.setContent("Contenido del articulo");

        var created = articleController.create(article, author.getId());
        var published = articleController.publish(created.getId());

        assertEquals(ArticleStatus.PUBLISHED, published.getStatus());
    }

    @Test
    void shouldSearchByKeyword() {
        Article article = new Article();
        article.setTitle("Devolucion de productos");
        article.setContent("Como devolver un producto");

        articleController.create(article, author.getId());
        var results = articleController.search("Devolucion");

        assertFalse(results.isEmpty());
    }

    @Test
    void shouldArchiveArticle() {
        Article article = new Article();
        article.setTitle("Articulo obsoleto");
        article.setContent("Ya no aplica");

        var created = articleController.create(article, author.getId());
        var archived = articleController.archive(created.getId());

        assertEquals(ArticleStatus.ARCHIVED, archived.getStatus());
    }

}