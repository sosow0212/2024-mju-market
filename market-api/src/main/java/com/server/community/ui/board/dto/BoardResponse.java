package com.server.community.ui.board.dto;

import com.server.community.domain.board.Board;

public record BoardResponse(
        Long boardId,
        String title,
        String content,
        Long memberId
) {

    public static BoardResponse from(final Board board) {
        return new BoardResponse(
                board.getId(),
                board.getPost().getTitle(),
                board.getPost().getContent(),
                board.getWriterId());
    }
}
