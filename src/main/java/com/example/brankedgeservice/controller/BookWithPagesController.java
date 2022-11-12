package com.example.brankedgeservice.controller;

import com.example.brankedgeservice.model.Book;
import com.example.brankedgeservice.model.BookWithPages;
import com.example.brankedgeservice.model.Category;
import com.example.brankedgeservice.model.Page;
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
        return Category.getCategoriesWithUrls();
    }

    //get books for category {category}
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
    public void setBookPagesUnseen(@PathVariable String bookTitle){
        restTemplate.put("http://"+pageServiceBaseUrl+"/pages/booktitle/" + bookTitle + "/setpagesunseen/", null);
    }

    //get Page from booktitle and pagenumber
    @GetMapping("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}")
    public Page getPageByBookTitleAndPageNumber(@PathVariable String bookTitle, @PathVariable int pageNumber){
        ResponseEntity<Page> responseEntityPage = restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/booktitle/"+bookTitle+"/pageNumber/"+pageNumber,
                HttpMethod.GET,null, new ParameterizedTypeReference<Page>() {});
        return responseEntityPage.getBody();
    }

    // get items from page
    @GetMapping("/interactivebooks/booktitle/{bookTitle}/pagenumber/{pagenumber}/items")
    public List<String> getItemsFromPage(@PathVariable String bookTitle, @PathVariable int pagenumber){
        return restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/booktitle/"+bookTitle+"/pagenumber/"+pagenumber+"/items",
                HttpMethod.GET,null, new ParameterizedTypeReference<List<String>>() {}).getBody();
    }

    //get pages seen in decimal
    @GetMapping("/interactivebooks/books/{bookTitle}/pagesseen")
    public  double getBookPagesSeen(@PathVariable String bookTitle){
        ResponseEntity<Double> responseEntity =  restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/booktitle/"+bookTitle+"/pagesunseen", HttpMethod.GET, null, new ParameterizedTypeReference<Double>() {});
        return responseEntity.getBody();

    }

    //update page given booktitle and pagenumber
    @PutMapping("/interactivebooks/pages/booktitle/{bookTitle}/pagenumber/{pageNumber}")
    public Page updatePageSeen(@PathVariable String bookTitle, @PathVariable int pageNumber){

        Page page = restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/booktitle/"+bookTitle+"/pagenumber/"+pageNumber,
                HttpMethod.GET,null, new ParameterizedTypeReference<Page>() {}).getBody();
        assert page != null;
        page.setSeen(true);

        ResponseEntity<Page> responseEntityPage = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages",
                HttpMethod.PUT, new HttpEntity<>(page), Page.class);
        return responseEntityPage.getBody();

    }



                                              /* --- BooksWithPages  --- */
    //get Book with pages
    @GetMapping("/interactivebooks/booktitle/{bookTitle}")
    public BookWithPages getBookWithPages(@PathVariable String bookTitle){
        Book book =  restTemplate.exchange("http://"+bookServiceBaseUrl+"/title/"+bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<Book>(){} ).getBody();

        List<Page> pages = restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/booktitle/"+bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>(){} ).getBody();

        return new BookWithPages(book, pages);

    }


    //update page given page
    @PutMapping("/interactivebooks/pages")
    public BookWithPages updatePage(@RequestBody Page page) {

        String bookTitle = page.getBookTitle();
        Book book =  restTemplate.exchange("http://"+bookServiceBaseUrl+"/title/"+bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<Book>(){} ).getBody();
        Page updatedPage = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages", HttpMethod.PUT, new HttpEntity<>(page), Page.class).getBody();
        List<Page> pages = restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/"+bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>(){} ).getBody();

        return new BookWithPages(book, pages);
    }


    // add Page to bookwithPages
    @PostMapping("/interactivebooks/pages")
    public BookWithPages addPage(@RequestParam int pageNumber, @RequestParam List<String> itemNames, @RequestParam String bookTitle ){
        Page page = restTemplate.postForObject("http://"+pageServiceBaseUrl+"/pages", new Page(pageNumber, itemNames, false, bookTitle), Page.class);

        Book book =  restTemplate.exchange("http://"+bookServiceBaseUrl+"/title/"+bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<Book>(){} ).getBody();
        List<Page> pages = restTemplate.exchange("http://"+pageServiceBaseUrl+"/pages/"+bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>(){} ).getBody();

        return new BookWithPages(book, pages);
    }



    //delete page given pageID

    @DeleteMapping("/interactivebooks/pages/pageId")
    public ResponseEntity deletePage(@PathVariable String id){
        restTemplate.delete("http://" + pageServiceBaseUrl + "/page/"+id);
        return ResponseEntity.ok().build();
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
//            List<Page> pages = restTemplate.exchange("http://" + pageServiceBaseUrl + "/pages/booktitle/" + bookTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<Page>>() {
//            }).getBody();
//
//            returnList.add(new BookWithPages(book, pages));
//
//        }
//
//        return returnList;
//
//    }




}
