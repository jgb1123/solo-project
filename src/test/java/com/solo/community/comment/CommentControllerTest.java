package com.solo.community.comment;

import com.google.gson.Gson;
import com.solo.community.comment.controller.CommentController;
import com.solo.community.comment.dto.CommentPatchDto;
import com.solo.community.comment.dto.CommentPostDto;
import com.solo.community.comment.dto.CommentResponseDto;
import com.solo.community.comment.entity.Comment;
import com.solo.community.comment.mapper.CommentMapper;
import com.solo.community.comment.service.CommentService;
import com.solo.community.member.service.MemberService;
import com.solo.community.security.config.SecurityConfig;
import com.solo.community.security.jwt.JwtTokenizer;
import com.solo.community.security.utils.CustomAuthorityUtils;
import com.solo.community.util.CommentDummy;
import com.solo.community.util.WithAuthMember;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
@MockBean({JpaMetamodelMappingContext.class, MemberService.class, JwtTokenizer.class, CustomAuthorityUtils.class})
@AutoConfigureRestDocs
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentMapper commentMapper;

    @Autowired
    private Gson gson;

    @Test
    @WithAuthMember(email = "hgd@gmail.com", roles = {"ADMIN", "USER"})
    void postCommentTest() throws Exception {
        //given
        Long boardId = 1L;
        Comment comment1 = CommentDummy.createComment1();
        CommentPostDto commentPostDto = CommentDummy.createPostDto();
        String content = gson.toJson(commentPostDto);
        given(commentMapper.commentPostDtoToComment(Mockito.any(CommentPostDto.class)))
                .willReturn(comment1);
        given(commentService.createComment(Mockito.anyString(), Mockito.anyLong(), Mockito.any(Comment.class)))
                .willReturn(comment1);
        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/comment/{boardId}", boardId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        actions.andExpect(status().isCreated())
                .andDo(document("post-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("boardId").description("????????? ?????????")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("commentContent").type(JsonFieldType.STRING).description("?????? ??????")
                                )
                        )
                ));
    }

    @Test
    void getCommentsTest() throws Exception {
        //given
        Long boardId = 1L;
        int page = 1;
        int size = 10;
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", Integer.toString(page));
        queryParams.add("size", Integer.toString(size));
        Comment comment1 = CommentDummy.createComment1();
        Comment comment2 = CommentDummy.createComment2();
        CommentResponseDto commentResponseDto1 = CommentDummy.createResponseDto1();
        CommentResponseDto commentResponseDto2 = CommentDummy.createResponseDto2();
        List<CommentResponseDto> responses = List.of(commentResponseDto1, commentResponseDto2);
        Page<Comment> commentPages = new PageImpl<>(List.of(comment1, comment2), PageRequest.of(page - 1, size,
                Sort.by("commentId").ascending()), 2);
        given(commentService.findComments(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(commentPages);
        given(commentMapper.commentsToCommentResponseDtos(Mockito.anyList()))
                .willReturn(responses);
        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/comment/{boardId}", boardId)
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        actions.andExpect(status().isOk())
                .andDo(document("get-comments",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("boardId").description("????????? ?????????")
                        ),
                        requestParameters(
                                List.of(
                                        parameterWithName("page").description("Page ??????"),
                                        parameterWithName("size").description("Page ??????")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("?????? ?????????"),
                                        fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                        fieldWithPath("data[].commentContent").type(JsonFieldType.STRING).description("?????? ??????"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING).description("????????? ??????"),

                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("????????? ??????"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("?????? ??? ???"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("?????? ????????? ???")
                                )
                        )
                ));
    }

    @Test
    @WithAuthMember(email = "hgd@gmail.com", roles = {"ADMIN", "USER"})
    void patchCommentTest() throws Exception {
        //given
        Long commentId = 1L;
        Comment comment = CommentDummy.createComment1();
        CommentPatchDto commentPatchDto = CommentDummy.createPatchDto();
        String content = gson.toJson(commentPatchDto);
        given(commentMapper.commentPatchDtoToComment(Mockito.any(CommentPatchDto.class)))
                .willReturn(comment);
        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/comment/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        actions.andExpect(status().isOk())
                .andDo(document("patch-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("?????? ?????????")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("commentContent").type(JsonFieldType.STRING).description("?????? ??????")
                                )
                        )
                ));
    }

    @Test
    @WithAuthMember(email = "hgd@gmail.com", roles = {"ADMIN", "USER"})
    void deleteCommentTest() throws Exception {
        //given
        Long commentId = 1L;
        doNothing().when(commentService).deleteComment(Mockito.anyString(), Mockito.anyLong());
        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/comment/{commentId}", commentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        actions.andExpect(status().isNoContent())
                .andDo(document("delete-comment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId").description("?????? ?????????")
                        )
                ));
    }
}
