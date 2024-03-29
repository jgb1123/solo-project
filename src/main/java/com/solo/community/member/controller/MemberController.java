package com.solo.community.member.controller;

import com.solo.community.dto.MultiResponseDto;
import com.solo.community.dto.SingleResponseDto;
import com.solo.community.member.dto.MemberPatchDto;
import com.solo.community.member.dto.MemberResponseDto;
import com.solo.community.member.entity.Member;
import com.solo.community.member.mapper.MemberMapper;
import com.solo.community.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberMapper memberMapper;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity getMember(@AuthenticationPrincipal String email) {
        Member foundMember = memberService.findMember(email);
        MemberResponseDto memberResponseDto = memberMapper.memberToMemberResponseDto(foundMember);
        return new ResponseEntity<>(new SingleResponseDto<>(memberResponseDto), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity patchMember(@Valid @RequestBody MemberPatchDto memberPatchDto,
                                      @AuthenticationPrincipal String email) {
        Member modifiedMember = memberMapper.memberPatchDtoToMember(memberPatchDto);
        memberService.updateMember(email, modifiedMember);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteMember(@AuthenticationPrincipal String email) {
        memberService.deleteMember(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
