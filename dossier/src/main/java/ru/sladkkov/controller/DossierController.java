package ru.sladkkov.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deal/document")
public class DossierController {

    @PostMapping("{applicationId}/send")
    public void send() {

    }

    @PostMapping("{applicationId}/sign")
    public void sign() {

    }

    @PostMapping("{applicationId}/code")
    public void code() {

    }
}
