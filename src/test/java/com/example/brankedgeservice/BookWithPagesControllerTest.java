package com.example.brankedgeservice;

import com.example.brankedgeservice.model.Book;
import com.example.brankedgeservice.model.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.brankedgeservice.model.Category.NIJNTJE;


@SpringBootTest
@AutoConfigureMockMvc
class BookWithPagesControllerTest {


    @Value("${pageservice.baseurl}")
    private String pageServiceBaseUrl;

    @Value("${bookservice.baseurl}")
    private String bookServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private final Book bookNijntje = new Book("Nijntje", "Dick Bruna", NIJNTJE, false, false, "https://i.postimg.cc/m2VJygQs/Nijntje.jpg", "https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg");
    private final Book bookNijntjeInDeSpeeltuin = new Book("Nijntje in de speeltuin", "Dick Bruna", NIJNTJE, false, true, "https://i.postimg.cc/RFrDZ6zc/Nijntjeindespeeltuin.jpg", "https://i.postimg.cc/8cm3319L/Nijntjeindespeeltuinachterkant.jpg");

    List<String> itemsPageOne = new ArrayList<>(Arrays.asList("nijntje", "mamaNijntje", "papaNijntje"));
    List<String> itemsPageTwo = new ArrayList<>(Arrays.asList("nijntje", "mamaNijntje", "papaNijntje", "auto"));

    Page pageOne = new Page(1, itemsPageOne, true, "Nijntje in de speeltuin");
    Page pageTwo = new Page(2, itemsPageTwo, false, "Nijntje in de speeltuin");

    List<Page> pages = new ArrayList<>(Arrays.asList(pageOne, pageTwo));
    List<Book> books = new ArrayList<>(Arrays.asList(bookNijntje, bookNijntjeInDeSpeeltuin));

    @BeforeEach
    public void initializeMockserver() {
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

    }

    @Test
    void getCategorieswithUrlsReturnsCategoriesWthUrls() {
    //TODO implement test => Niet zo simpel omdat Categroy een ENUM is.... Desnoods test open laten... Alles omzetten is veel werk....
    }

    @Test
    void getBooksByCategory() {

    }

    @Test
    void updateBook() {
    }

    @Test
    void setBookPagesUnseen() {
    }

    @Test
    void getPageByBookTitleAndPageNumber() {
    }

    @Test
    void getItemsFromPage() {
    }

    @Test
    void getBookPagesSeen() {
    }

    @Test
    void updatePageSeen() {
    }

    @Test
    void getBookWithPages() {
    }

    @Test
    void updatePage() {
    }

    @Test
    void addPage() {
    }

    @Test
    void deletePage() {
    }
}