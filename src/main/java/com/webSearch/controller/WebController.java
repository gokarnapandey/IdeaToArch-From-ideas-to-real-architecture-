package com.webSearch.controller;

import com.webSearch.service.WebService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ask")
@RequiredArgsConstructor
public class WebController {

    private final WebService webService;

    @PostMapping
    public String ask(@RequestBody String message){
        return webService.webSearch(message);
    }
}
