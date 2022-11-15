package com.example.brankedgeservice;

import com.example.brankedgeservice.model.Book;
import com.example.brankedgeservice.model.BookWithPages;
import com.example.brankedgeservice.model.Category;
import com.example.brankedgeservice.model.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.brankedgeservice.model.Category.NIJNTJE;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@SpringBootTest
@AutoConfigureMockMvc
class BookWithPagesControllerUnitTests {


    @Value("${pageservice.baseurl}")
    private String pageServiceBaseUrl;

    @Value("${bookservice.baseurl}")
    private String bookServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockRestServiceServer mockServer;

    private final Book bookNijntje = new Book("Nijntje", "Dick Bruna", NIJNTJE, false, false, "https://i.postimg.cc/m2VJygQs/Nijntje.jpg", "https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg");
    private final Book bookNijntjeInDeSpeeltuin = new Book("Nijntje in de speeltuin", "Dick Bruna", NIJNTJE, false, true, "https://i.postimg.cc/RFrDZ6zc/Nijntjeindespeeltuin.jpg", "https://i.postimg.cc/8cm3319L/Nijntjeindespeeltuinachterkant.jpg");


    List<String> itemsPageOne = new ArrayList<>(Arrays.asList("nijntje", "mamaNijntje", "papaNijntje"));
    List<String> itemsPageTwo = new ArrayList<>(Arrays.asList("nijntje", "mamaNijntje", "papaNijntje", "auto"));


    Page pageOneNijntjeInDeSpeeltuin = new Page(1, itemsPageOne, true, "Nijntje in de speeltuin");
    Page pageTwoNijntjeInDeSpeeltuin = new Page(2, itemsPageTwo, false, "Nijntje in de speeltuin");


    List<Page> pages = new ArrayList<>(Arrays.asList(pageOneNijntjeInDeSpeeltuin, pageTwoNijntjeInDeSpeeltuin));

    BookWithPages NijntjeInDeSpeeltuin = new BookWithPages(bookNijntjeInDeSpeeltuin, pages);


    List<Book> books = new ArrayList<>(Arrays.asList(bookNijntje, bookNijntjeInDeSpeeltuin));

    @BeforeEach
    void initializeMockserver() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getCategorieswithUrlsReturnsCategoriesWthUrls() {
    //TODO implement test => Niet zo simpel omdat Categroy een ENUM is.... Desnoods test open laten... Alles omzetten is veel werk....
    }

    @Test
    void whenGetBookByCategoryNijntje_then_returnJSONBook() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://"+bookServiceBaseUrl+"/books/category/NIJNTJE")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(books)));

        mockMvc.perform(get("/interactivebooks/booksbycategory/{category}", "NIJNTJE"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Nijntje")))
                .andExpect(jsonPath("$[0].author", is("Dick Bruna")))
                .andExpect(jsonPath("$[0].category", is(NIJNTJE)))
                .andExpect(jsonPath("$[0].favorite", is(false)))
                .andExpect(jsonPath("$[0].available", is(false)))
                .andExpect(jsonPath("$[0].coverImageUrl", is("https://i.postimg.cc/m2VJygQs/Nijntje.jpg")))
                .andExpect(jsonPath("$[0].backCoverImageUrl", is("https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg")))
                .andExpect(jsonPath("$[1].title", is("Nijntje in de speeltuin")))
                .andExpect(jsonPath("$[1].author", is("Dick Bruna")))
                .andExpect(jsonPath("$[1].category", is(NIJNTJE)))
                .andExpect(jsonPath("$[1].favorite", is(false)))
                .andExpect(jsonPath("$[1].available", is(true)))
                .andExpect(jsonPath("$[1].coverImageUrl", is("https://i.postimg.cc/RFrDZ6zc/Nijntjeindespeeltuin.jpg")))
                .andExpect(jsonPath("$[1].backCoverImageUrl", is("https://i.postimg.cc/8cm3319L/Nijntjeindespeeltuinachterkant.jpg")));

    }

    @Test
    void whenUpdateBook_thenreturnBookJSON() throws Exception{

        Book updatedBook = new Book("Nijntje", "Dick Bruna", NIJNTJE, false, true, "https://i.postimg.cc/m2VJygQs/Nijntje.jpg", "https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg");


        //put update book nijntje
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + bookServiceBaseUrl + "/books")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(updatedBook)));

        mockMvc.perform(put("/interactivebooks/updatebook")
                .param("title", updatedBook.getTitle().toString())
                .param("author", updatedBook.getAuthor().toString())
                .param("category", updatedBook.getCategory().toString())
                .param("favorite", String.valueOf(updatedBook.isFavorite()))
                .param("available", String.valueOf(updatedBook.isAvailable()))
                .param("coverImageUrl", updatedBook.getCoverImageUrl())
                .param("backCoverImageUrl", updatedBook.getBackCoverImageUrl())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Nijntje")))
                .andExpect(jsonPath("$.author", is("Dick Bruna")))
                .andExpect(jsonPath("$.category", is(NIJNTJE)))
                .andExpect(jsonPath("$.favorite", is(false)))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.coverImageUrl", is("https://i.postimg.cc/m2VJygQs/Nijntje.jpg")))
                .andExpect(jsonPath("$.backCoverImageUrl", is("https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg")));



    }

    @Test
    void WhensetBookPagesUnseen_thenreturn() {
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