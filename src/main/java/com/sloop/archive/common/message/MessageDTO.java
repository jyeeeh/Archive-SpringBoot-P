package com.sloop.archive.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MessageDTO {

    private String message;             //  사용자에게 전달할 메세지
    private String redirectUri;         //  리다이렉트 URI
    private RequestMethod method;       //  HTTP 요청 메서드
    private Map<String , Object > data; //  화면(VIEW)으로 전달할 데이터(파라미터)
}
