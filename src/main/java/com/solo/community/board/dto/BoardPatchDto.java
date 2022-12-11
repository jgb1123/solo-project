package com.solo.community.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPatchDto {
    private String boardTitle;
    private String boardContent;
    private Long memberId;
}
