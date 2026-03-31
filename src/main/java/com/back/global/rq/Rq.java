package com.back.global.rq;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request; // requestScope
    private final HttpServletResponse response;
    private final MemberService memberService;

    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");

        response.addCookie(
                cookie
        );
    }

    public Member getActor() {

        String authorizationHeader = request.getHeader("Authorization");

        String apiKey = null;

        //헤더 방식 vs 쿠키 방식
        if (authorizationHeader != null) {
//            throw new ServiceException("401-1", "인증 정보가 헤더에 존재하지 않습니다.");
            if (!authorizationHeader.startsWith("Bearer ")) {
                throw new ServiceException("401-2", "잘못된 형식의 인증데이터 입니다.");
            }
            apiKey = authorizationHeader.replace("Bearer ", "");
        } else {
            apiKey = request.getCookies() == null ? ""
                    : Arrays.stream(request.getCookies())
                      .filter(cookie -> cookie.getName().equals("apiKey"))
                      .map(Cookie::getValue)
                      .findFirst()
                      .orElse("");
        }

        if (apiKey.isBlank()) {
            throw new ServiceException("401-3", "인증 정보가 유효하지 않습니다.");
        }

        return memberService.findByApiKey(apiKey).orElseThrow(
                () -> new ServiceException("401-1", "유효하지 않은 API 키입니다.")
        );
    }
}
