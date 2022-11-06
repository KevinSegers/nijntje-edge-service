package com.example.brankedgeservice.controller;

import com.example.brankedgeservice.model.Book;
import com.example.brankedgeservice.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
public class BookWithPagesController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pageservice.baseurl}")
    private String pageServiceBaseUrl;

    @Value("${bookservice.baseurl}")
    private String bookServiceBaseUrl;

    @GetMapping("/interactivebooks/categorieswithurls")
    public List<Map<String, String>> getCategorieswithUrls() {
        return Category.getCategoriesWithUrls();
    }

    @GetMapping("/interactivebooks/booksbycategory/{category}")
    public List<Book> getBooksByCategory(@PathVariable String category) {
        ResponseEntity<List<Book>> response = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/category/" + category, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        return response.getBody();
    }

    @PutMapping("/interactivebooks/book")
    public Book updateBook(@RequestBody Book book) {
        ResponseEntity<Book> responseEntityBook = restTemplate.exchange("http://" + bookServiceBaseUrl + "/book", HttpMethod.PUT, new HttpEntity<>(book), Book.class);
        return responseEntityBook.getBody();
    }

//    @GetMapping("/interactivebooks/booksbycategorywithpages/{category}")
//    // => niet nodig om alle pagina's mee te geven bij het ophalen van alle boeken
//    public List<BookWithPages> getBooksByCategoryWithPages(@PathVariable Category category) {
//        List<BookWithPages> returnList = new ArrayList<>();
//
//
//        ResponseEntity<List<Book>> responseEntityBooks = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/category/" + category, HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
//        });
//
//        List<Book> books = responseEntityBooks.getBody();
//
//
//        assert books != null;
//        for (Book book : books) {
//            String bookTitle = book.getTitle();
//            List<Page> pages = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/bookTitle/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>() {
//            }).getBody();
//
//            returnList.add(new BookWithPages(book, pages));
//
//        }
//
//        return returnList;
//
//    }

//    @GetMapping("/interactivebooks/book/{bookTitle}/page/{pageNumber}") //bekijken!! sonarcloud geeft vulnerability aan, endpoint is niet echt nodig nu
//    public Page getPageByBooktitleAndPagenumber(@PathVariable String bookTitle, @PathVariable int pageNumber) {
//        return restTemplate.getForObject("http://" + pageServiceBaseUrl + "/pages/bookTitle/" + bookTitle + "/pageNumber/" + pageNumber, Page.class);
//    }


}
