package com.example.brankedgeservice.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Category {
    NIJNTJE("Nijntje", "https://i.postimg.cc/3wpjLy5b/nijntje-cover.jpg"),
    BUMBA("Bumba", "https://i.postimg.cc/DZr9Kysx/bumba-cover.jpg"),
    DRIBBEL("Dribbel", "https://i.postimg.cc/ydRvMQqB/dribbel-cover.jpg");

    public final String label;
    public final String url;

    Category(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public static List<Map<String, String>> getCategorieswithUrls() {
        return Arrays.stream(Category.values()).map(e -> {
            Map<String, String> m = new HashMap<>();
            m.put("label", e.getLabel());
            m.put("url", e.getUrl());
            return m;
        }).collect(Collectors.toList());
    }

}
