package com.solo.community.security.handler;

import com.solo.community.member.entity.Member;
import com.solo.community.member.service.MemberService;
import com.solo.community.security.jwt.JwtTokenizer;
import com.solo.community.security.utils.CustomAuthorityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final MemberService memberService;

    public OAuth2MemberSuccessHandler(JwtTokenizer jwtTokenizer,
                                      CustomAuthorityUtils customAuthorityUtils,
                                      MemberService memberService) {
        this.jwtTokenizer = jwtTokenizer;
        this.customAuthorityUtils = customAuthorityUtils;
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var oAuth2User = (OAuth2User)authentication.getPrincipal();
        String email = String.valueOf(oAuth2User.getAttributes().get("email"));
        String name = String.valueOf(oAuth2User.getAttributes().get("name"));

        List<String> authorities = customAuthorityUtils.createRoles(email);
        if(!existMemberCheck(email)){
            saveMember(email, name);
        }
        String accessToken = delegateAccessToken(email, authorities);
        String refreshToken = delegateRefreshToken(email);
        writeTokenResponse(response, accessToken, refreshToken);
    }

    private boolean existMemberCheck(String email) {
        return memberService.existsEmail(email);
    }

    private void saveMember(String email, String name) {
        Member member = new Member();
        member.changeSignUpInfo(email, name);
        memberService.createMember(member);
    }

    private String delegateAccessToken(String email, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", authorities);

        String subject = email;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    private String delegateRefreshToken(String email) {
        String subject = email;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refreshToken;
    }

    private void writeTokenResponse(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.addHeader("AccessToken", accessToken);
        response.addHeader("RefreshToken", refreshToken);
        response.setContentType("application/json;charset=UTF-8");
        var writer = response.getWriter();
        writer.println("AccessToken : " + accessToken);
        writer.println("RefreshToken : " + refreshToken);
    }
}