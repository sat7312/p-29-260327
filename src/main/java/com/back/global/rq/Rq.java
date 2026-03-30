package com.back.global.rq;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope // 각 HTTP 요청마다 새로운 Rq 객체가 생성, 요청이 끝나면 해당 객체는 소멸
public class Rq {

}
