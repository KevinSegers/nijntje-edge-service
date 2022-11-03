package com.example.brankedgeservice.controller;

import com.example.brankedgeservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
public class BookWithPagesController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pageservice.baseurl}")
    private String pageServiceBaseUrl;

    @Value("${bookservice.baseurl}")
    private String bookServiceBaseUrl;


    //Nu niet meer nodig omdat ze nu samen in 1 endpoint zitten. Nog even laten staan voor referentie
//    @GetMapping("/interactivebooks/categories")
//    public List<String> getCategories() {
//        return EnumSet.allOf(Category.class).stream().map(Category::getLabel).collect(Collectors.toList());
//    }
//    @GetMapping("/interactivebooks/categories/urls")
//    public List<String> getCategoriesUrls() {
//        return EnumSet.allOf(Category.class).stream().map(Category::getUrl).collect(Collectors.toList());
//    }

    @GetMapping("/interactivebooks/categorieswithurls")
    public List<Map<String, String>> getCategorieswithUrls() {
        return Category.getCategoriesWithUrls();
    }

    @GetMapping("/interactivebooks/category/{category}")
    public List<BookWithPages> getBooksByCategory(@PathVariable Category category){
        List<BookWithPages> returnList = new ArrayList<>();


        ResponseEntity<List<Book>> responseEntityBooks =
                restTemplate.exchange("http://" + bookServiceBaseUrl + "/books/category/" + category,
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });

        List<Book> books = responseEntityBooks.getBody();


        assert books != null;
        for (Book book: books) {
            String bookTitle = book.getTitle();
           List<Page> pages =
                    restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/bookTitle/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>() {
                            }).getBody();

            returnList.add(new BookWithPages(book, pages));

        }

        return returnList;

    }

    @GetMapping("/interactivebooks/book/{bookTitle}/page/{pageNumber}") //bekijken!! sonarcloud geeft vulnerability aan
    public Page getPageByBooktitleAndPagenumber(@PathVariable String bookTitle, @PathVariable int pageNumber){
        return restTemplate.getForObject("http://"+pageServiceBaseUrl+"/pages/bookTitle/"+bookTitle+"/pageNumber/"+pageNumber, Page.class );
    }


}
