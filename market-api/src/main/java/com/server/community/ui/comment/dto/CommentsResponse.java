package com.server.community.ui.comment.dto;

import com.server.community.domain.comment.Comment;

import java.util.List;
import java.util.stream.Collectors;

public record CommentsResponse(
        List<CommentResponse> comments
) {

    public static CommentsResponse from(final List<Comment> comments) {
        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CommentsResponse::new));
    }
}
