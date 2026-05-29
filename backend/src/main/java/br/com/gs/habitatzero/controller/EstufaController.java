package br.com.gs.habitatzero.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estufas")
public class EstufaController {

    @GetMapping
    public String index() {
        return "Teste de estufa";
    }

}
