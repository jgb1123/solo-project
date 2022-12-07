package com.solo.community.member;

import com.solo.community.member.entity.Member;
import com.solo.community.member.repository.MemberRepository;
import com.solo.community.member.service.MemberService;
import com.solo.community.util.MemberDummy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;


    @Test
    public void createMemberTest() {
        //given
        Member member1 = MemberDummy.createMember1();
        given(memberRepository.save(Mockito.any(Member.class)))
                .willReturn(member1);
        //when
        Member savedMember = memberService.createMember(member1);
        //then
        assertThat(member1.getEmail()).isEqualTo(savedMember.getEmail());
    }

    @Test
    public void findMemberTest() {
        //given
        Member member1 = MemberDummy.createMember1();
        given(memberRepository.findById(Mockito.anyLong()))
                .willReturn(Optional.of(member1));
        //when
        Member foundMember = memberService.findMember(1L);
        //then
        assertThat(member1.getEmail()).isEqualTo(foundMember.getEmail());
    }

    @Test
    public void findMembersTest() {
        //given
        Member member1 = MemberDummy.createMember1();
        Member member2 = MemberDummy.createMember2();
        given(memberRepository.findAll(PageRequest.of(0, 10, Sort.by("nickname").ascending())))
                .willReturn(new PageImpl<>(List.of(member1, member2), PageRequest.of(0, 10, Sort.by("nickname").ascending()), 2));
        //when
        Page<Member> pageMembers = memberService.findMembers(1, 10);
        //then
        assertThat(member1.getEmail()).isEqualTo(pageMembers.getContent().get(0).getEmail());
        assertThat(member2.getEmail()).isEqualTo(pageMembers.getContent().get(1).getEmail());
    }

    @Test
    public void updateMemberTest() {
        //given
        Member member1 = MemberDummy.createMember1();
        Member modifiedMember = MemberDummy.createMember2();
        given(memberRepository.findById(Mockito.anyLong()))
                .willReturn(Optional.of(member1));
        //when
        Member updatedMember = memberService.updateMember(1L, modifiedMember);

        //then
        assertThat(modifiedMember.getEmail()).isEqualTo(updatedMember.getEmail());
    }
}
