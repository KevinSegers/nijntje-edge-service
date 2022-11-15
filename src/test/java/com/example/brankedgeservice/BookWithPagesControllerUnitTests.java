package com.example.brankedgeservice;

import com.example.brankedgeservice.model.Book;
import com.example.brankedgeservice.model.BookWithPages;
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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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
    void whengetCategorieswithUrls_ReturnsCategoriesWthUrlsJSON() throws Exception {

        mockMvc.perform(get("/interactivebooks/categorieswithurls"))
                .andExpect(jsonPath("$[0].label").value("Nijntje"))
                .andExpect(jsonPath("$[0].url").value("https://i.postimg.cc/gkQmtdRD/nijntje-cover.jpg"))
                .andExpect(jsonPath("$[1].label").value("Bumba"))
                .andExpect(jsonPath("$[1].url").value("https://i.postimg.cc/DZr9Kysx/bumba-cover.jpg"))
                .andExpect(jsonPath("$[2].label").value("Dribbel"))
                .andExpect(jsonPath("$[2].url").value("https://i.postimg.cc/ydRvMQqB/dribbel-cover.jpg"));
    }

    @Test
    void whenGetBookByCategoryNijntje_then_returnJSONBook() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + bookServiceBaseUrl + "/books/category/NIJNTJE")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(books)));

        mockMvc.perform(get("/interactivebooks/booksbycategory/{category}", "NIJNTJE"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Nijntje")))
                .andExpect(jsonPath("$[0].author", is("Dick Bruna")))
                .andExpect(jsonPath("$[0].category", is("NIJNTJE")))
                .andExpect(jsonPath("$[0].favorite", is(false)))
                .andExpect(jsonPath("$[0].available", is(false)))
                .andExpect(jsonPath("$[0].coverImageUrl", is("https://i.postimg.cc/m2VJygQs/Nijntje.jpg")))
                .andExpect(jsonPath("$[0].backCoverImageUrl", is("https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg")))
                .andExpect(jsonPath("$[1].title", is("Nijntje in de speeltuin")))
                .andExpect(jsonPath("$[1].author", is("Dick Bruna")))
                .andExpect(jsonPath("$[1].category", is("NIJNTJE")))
                .andExpect(jsonPath("$[1].favorite", is(false)))
                .andExpect(jsonPath("$[1].available", is(true)))
                .andExpect(jsonPath("$[1].coverImageUrl", is("https://i.postimg.cc/RFrDZ6zc/Nijntjeindespeeltuin.jpg")))
                .andExpect(jsonPath("$[1].backCoverImageUrl", is("https://i.postimg.cc/8cm3319L/Nijntjeindespeeltuinachterkant.jpg")));

    }

    @Test
    void whenUpdateBook_thenreturnBookJSON() throws Exception {

        Book updatedBook = new Book("Nijntje", "Dick Bruna", NIJNTJE, false, true, "https://i.postimg.cc/m2VJygQs/Nijntje.jpg", "https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg");


        //put update book nijntje
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + bookServiceBaseUrl + "/books")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(updatedBook)));

        mockMvc.perform(put("/interactivebooks/updatebook")
                        .content(mapper.writeValueAsString(updatedBook))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Nijntje")))
                .andExpect(jsonPath("$.author", is("Dick Bruna")))
                .andExpect(jsonPath("$.category", is("NIJNTJE")))
                .andExpect(jsonPath("$.favorite", is(false)))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.coverImageUrl", is("https://i.postimg.cc/m2VJygQs/Nijntje.jpg")))
                .andExpect(jsonPath("$.backCoverImageUrl", is("https://i.postimg.cc/FHRqWXZJ/Nijntjeachterkant.jpg")));


    }

    @Test
    void whenSetBookPagesUnseen_thenreturnPages() throws Exception {

        Page pageOneNijntjeInDeSpeeltuinUpdated = new Page(1, itemsPageOne, false, "Nijntje in de speeltuin");
        List<Page> pages = new ArrayList<>(Arrays.asList(pageOneNijntjeInDeSpeeltuinUpdated, pageTwoNijntjeInDeSpeeltuin));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin/setpagesunseen")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pages)));

        mockMvc.perform(put("/interactivebooks/books/{bookTitle}/setpagesunseen", "Nijntje in de speeltuin").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].seen", is(false)))
                .andExpect(jsonPath("$[1].seen", is(false)));

    }

    @Test
    void whenGetPageByBookTitleAndPageNumberOne_thenReturnPageJSON() throws Exception {

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin/pagenumber/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pageOneNijntjeInDeSpeeltuin)));

        mockMvc.perform(get("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}", "Nijntje in de speeltuin", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.seen", is(true)))
                .andExpect(jsonPath("$.bookTitle", is("Nijntje in de speeltuin")));

    }

    @Test
    void whenGetItemsFromPageOne_thenReturnItemsPageOneJSON() throws Exception {

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin/pagenumber/1/items")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(itemsPageOne)));

        mockMvc.perform(get("/interactivebooks/booktitle/{bookTitle}/pagenumber/{pageNumber}/items", "Nijntje in de speeltuin", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("nijntje")))
                .andExpect(jsonPath("$[1]", is("mamaNijntje")))
                .andExpect(jsonPath("$[2]", is("papaNijntje")));

    }

    @Test
    void whenGetBookPagesSeen_thenReturnDoubleJSON() throws Exception {

        double pagesSeen = 0.50;

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin/pagesseen")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pagesSeen)));


        mockMvc.perform(get("/interactivebooks/books/{bookTitle}/pagesseen", "Nijntje in de speeltuin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.50)));
    }

    @Test
    void whenGetBookPagesSeenButNoPages_thenReturnZero() throws Exception {

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje/pagesseen")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(null)));


        mockMvc.perform(get("/interactivebooks/books/{bookTitle}/pagesseen", "Nijntje")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.0)));
    }



    @Test
    void whenUpdatePageSeen_returnUpdatePageJSON() throws Exception {

        Page pageTwoNijntjeInDeSpeeltuinUpdated = new Page(2, itemsPageTwo, true, "Nijntje in de speeltuin");

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin/pagenumber/2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pageTwoNijntjeInDeSpeeltuin)));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pageTwoNijntjeInDeSpeeltuinUpdated)));

        mockMvc.perform(put("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}", "Nijntje in de speeltuin", 2)
                        .content(mapper.writeValueAsString(pageTwoNijntjeInDeSpeeltuin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber", is(2)))
                .andExpect(jsonPath("$.items", hasSize(4)))
                .andExpect(jsonPath("$.seen", is(true)))
                .andExpect(jsonPath("$.bookTitle", is("Nijntje in de speeltuin")));
    }

    @Test
    void whenGetBookWithPages_thenReturnBookWithPagesJSON() throws Exception {

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + bookServiceBaseUrl + "/books/title/Nijntje%20in%20de%20speeltuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(bookNijntjeInDeSpeeltuin)));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pages)));

        mockMvc.perform(get("/interactivebooks/book/{bookTitle}", "Nijntje in de speeltuin")
                        .content(mapper.writeValueAsString(NijntjeInDeSpeeltuin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Nijntje in de speeltuin")))
                .andExpect(jsonPath("$.author", is("Dick Bruna")))
                .andExpect(jsonPath("$.favorite", is(false)))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.coverImageUrl", is("https://i.postimg.cc/RFrDZ6zc/Nijntjeindespeeltuin.jpg")))
                .andExpect(jsonPath("$.backCoverImageUrl", is("https://i.postimg.cc/8cm3319L/Nijntjeindespeeltuinachterkant.jpg")))
                .andExpect(jsonPath("$.pagesFromBook", hasSize(2)))
                .andExpect(jsonPath("$.pagesFromBook[0].pageNumber", is(1)))
                .andExpect(jsonPath("$.pagesFromBook[0].items", hasSize(3)))
                .andExpect(jsonPath("$.pagesFromBook[0].seen", is(true)))
                .andExpect(jsonPath("$.pagesFromBook[0].bookTitle", is("Nijntje in de speeltuin")))
                .andExpect(jsonPath("$.pagesFromBook[1].pageNumber", is(2)))
                .andExpect(jsonPath("$.pagesFromBook[1].items", hasSize(4)))
                .andExpect(jsonPath("$.pagesFromBook[1].seen", is(false)))
                .andExpect(jsonPath("$.pagesFromBook[1].bookTitle", is("Nijntje in de speeltuin")));
    }

    @Test
    void whenUpdatePage_thenReturnBookWithUpdatedPageJSON() throws Exception {

        Page pageTwoNijntjeInDeSpeeltuinUpdated = new Page(2, itemsPageTwo, true, "Nijntje in de speeltuin");
        List<Page> pages = new ArrayList<>(Arrays.asList(pageOneNijntjeInDeSpeeltuin, pageTwoNijntjeInDeSpeeltuinUpdated));
        BookWithPages bookUpdated = new BookWithPages(bookNijntjeInDeSpeeltuin, pages);


        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + bookServiceBaseUrl + "/books/title/Nijntje%20in%20de%20speeltuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(bookNijntjeInDeSpeeltuin)));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pageTwoNijntjeInDeSpeeltuinUpdated)));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pages)));

        mockMvc.perform(put("/interactivebooks/pages")
                        .content(mapper.writeValueAsString(bookUpdated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Nijntje in de speeltuin")))
                .andExpect(jsonPath("$.pagesFromBook", hasSize(2)))
                .andExpect(jsonPath("$.pagesFromBook[1].seen", is(true))); // Updated value

    }

    @Test
    void whenAddPage_thenReturnBookWithPagesJSON() throws Exception {
        List<String> itemsPageThree = new ArrayList<>(Arrays.asList("nijntje", "mamaNijntje"));
        Page pageThreeNijntjeInDeSpeeltuin = new Page(3, itemsPageThree, false, "Nijntje in de speeltuin");
        List<Page> pages = new ArrayList<>(Arrays.asList(pageOneNijntjeInDeSpeeltuin, pageTwoNijntjeInDeSpeeltuin, pageThreeNijntjeInDeSpeeltuin));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pageThreeNijntjeInDeSpeeltuin)));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + bookServiceBaseUrl + "/books/title/Nijntje%20in%20de%20speeltuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(bookNijntjeInDeSpeeltuin)));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(pages)));

        mockMvc.perform(post("/interactivebooks/pages")
                        .param("pageNumber", String.valueOf(pageThreeNijntjeInDeSpeeltuin.getPageNumber()))
                        .param("items", String.valueOf(pageThreeNijntjeInDeSpeeltuin.getItems()))
                        .param("seen", String.valueOf(pageThreeNijntjeInDeSpeeltuin.isSeen()))
                        .param("bookTitle", pageThreeNijntjeInDeSpeeltuin.getBookTitle())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagesFromBook", hasSize(3)))
                .andExpect(jsonPath("$.pagesFromBook[2].pageNumber", is(3)))
                .andExpect(jsonPath("$.pagesFromBook[2].items", hasSize(2)))
                .andExpect(jsonPath("$.pagesFromBook[2].seen", is(false)))
                .andExpect(jsonPath("$.pagesFromBook[2].bookTitle", is("Nijntje in de speeltuin")));

    }

    @Test
    void whendeletePage_thenReturnStatusOKJSON() throws Exception {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + pageServiceBaseUrl + "/pages/booktitle/Nijntje%20in%20de%20speeltuin/pagenumber/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );
        mockMvc.perform(delete("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}", "Nijntje in de speeltuin", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}