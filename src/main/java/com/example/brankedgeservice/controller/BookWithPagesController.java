package com.example.brankedgeservice.controller;

import com.example.brankedgeservice.model.*;
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

    /*      ---- BOOK ----     */

    //get categories with url
    @GetMapping("/interactivebooks/categorieswithurls")
    public List<Map<String, String>> getCategorieswithUrls() {
        return CategoryWithUrl.getCategoriesWithUrls();
    }

    //get books for category
    @GetMapping("/interactivebooks/booksbycategory/{category}")
    public List<Book> getBooksByCategory(@PathVariable String category) {
        Category.valueOf(category);
        ResponseEntity<List<Book>> response = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/category/" + category, HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
        });
        return response.getBody();
    }

    //update book
    @PutMapping("/interactivebooks/updatebook")
    public Book updateBook(@RequestBody Book book) {
        ResponseEntity<Book> responseEntityBook = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books", HttpMethod.PUT, new HttpEntity<>(book), Book.class);
        return responseEntityBook.getBody();
    }

    /*      ---- PAGES ----     */


    // set pages from book unseen
    @PutMapping("/interactivebooks/books/{bookTitle}/setpagesunseen")
    public List<Page> setBookPagesUnseen(@PathVariable String bookTitle) {
        ResponseEntity<List<Page>> responseEntityPage = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle + "/setpagesunseen",
                HttpMethod.PUT, null, new ParameterizedTypeReference<List<Page>>() {
                });
        return responseEntityPage.getBody();
    }

    //get Page from booktitle and pagenumber
    @GetMapping("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}")
    public Page getPageByBookTitleAndPageNumber(@PathVariable String bookTitle, @PathVariable int pageNumber) {
        ResponseEntity<Page> responseEntityPage = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle + "/pagenumber/" + pageNumber,
                HttpMethod.GET, null, new ParameterizedTypeReference<Page>() {
                });
        return responseEntityPage.getBody();
    }

    // get items from page
    @GetMapping("/interactivebooks/booktitle/{bookTitle}/pagenumber/{pageNumber}/items")
    public List<String> getItemsFromPage(@PathVariable String bookTitle, @PathVariable int pageNumber) {
        return restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle + "/pagenumber/" + pageNumber + "/items",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {
                }).getBody();
    }

    //get pages seen in decimal
    @GetMapping("/interactivebooks/books/{bookTitle}/pagesseen")
    public double getBookPagesSeen(@PathVariable String bookTitle) {
        ResponseEntity<Double> responseEntity = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle + "/pagesseen", HttpMethod.GET, null, new ParameterizedTypeReference<Double>() {
        });

        if (responseEntity.getBody() == null) {
            return 0;
        }
        return responseEntity.getBody();

    }

    //update page to seen given booktitle and pagenumber
    @PutMapping("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}")
    public Page updatePageSeen(@PathVariable String bookTitle, @PathVariable int pageNumber) {

        Page page = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle + "/pagenumber/" + pageNumber,
                HttpMethod.GET, null, new ParameterizedTypeReference<Page>() {
                }).getBody();
        assert page != null;
        page.setSeen(true);

        ResponseEntity<Page> responseEntityPage = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages",
                HttpMethod.PUT, new HttpEntity<>(page), Page.class);
        return responseEntityPage.getBody();

    }


    /* --- BooksWithPages  --- */


    //get Book with pages
    @GetMapping("/interactivebooks/book/{bookTitle}")
    public BookWithPages getBookWithPages(@PathVariable String bookTitle) {
        Book book = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/title/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<Book>() {
        }).getBody();

        List<Page> pages = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>() {
        }).getBody();

        assert book != null;
        return new BookWithPages(book, pages);

    }


    //update page given page
    @PutMapping("/interactivebooks/pages")
    public BookWithPages updatePage(@RequestBody Page page) {

        String bookTitle = page.getBookTitle();
        Book book = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/title/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<Book>() {
        }).getBody();
        restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages", HttpMethod.PUT, new HttpEntity<>(page), Page.class).getBody();
        List<Page> pages = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>() {
        }).getBody();

        assert book != null;
        return new BookWithPages(book, pages);
    }


    // add Page to bookwithPages
    @PostMapping("/interactivebooks/pages")
    public BookWithPages addPage(@RequestParam int pageNumber, @RequestParam List<String> items, @RequestParam String bookTitle) {
        restTemplate.postForObject("http://" + pageServiceBaseUrl + "/pages", new Page(pageNumber, items, false, bookTitle), Page.class);

        Book book = restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/title/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<Book>() {
        }).getBody();

        List<Page> pages = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>() {
        }).getBody();

        assert book != null;
        return new BookWithPages(book, pages);
    }


    //delete page
    @DeleteMapping("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}")
    public ResponseEntity deletePage(@PathVariable String bookTitle, @PathVariable int pageNumber) {
        restTemplate.delete("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle +"/pagenumber/" + pageNumber);
        return ResponseEntity.ok().build();
    }

}
