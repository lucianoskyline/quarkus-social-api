package com.quarkussocial.rest.dto;

import com.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String text;

    private LocalDateTime datetime;


    public static PostResponse fromEntity(Post post){
        var response=new PostResponse();
        response.setDatetime(post.getDatetime());
        response.setText(post.getPostText());
        return response;
    }

}
