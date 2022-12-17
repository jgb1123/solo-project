package com.solo.community.board.service;

import com.solo.community.board.entity.Board;
import com.solo.community.board.repository.BoardRepository;
import com.solo.community.exception.BusinessLogicException;
import com.solo.community.exception.ExceptionCode;
import com.solo.community.member.entity.Member;
import com.solo.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public Board createBoard(String email, Board board) {
        Member foundMember = memberService.findVerifiedMember(email);
        board.changeMember(foundMember);
        return boardRepository.save(board);
    }

    public Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }

    public Page<Board> findBoards(int page, int size) {
        return boardRepository.findAll(PageRequest.of(page - 1, size, Sort.by("boardId").descending()));
    }

    public Board updateBoard(Long boardId, Board modifiedBoard, String email) {
        Board foundBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        if(!foundBoard.getMember().getEmail().equals(email))
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_BOARD);

        foundBoard.changeInfo(modifiedBoard);
        return foundBoard;
    }

    public void deleteBoard(Long boardId, String email) {
        Board foundBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        if(!foundBoard.getMember().getEmail().equals(email))
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_BOARD);

        boardRepository.delete(foundBoard);
    }
}
