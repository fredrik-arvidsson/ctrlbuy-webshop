package com.ctrlbuy.webshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SwedishRoutesController {

    // TA BORT /varukorg från här - det hanteras nu i CartController

    // Lägg till andra svenska routes här om du behöver, t.ex:

    // @GetMapping("/om-oss")
    // public String aboutUs() {
    //     return "redirect:/about";
    // }

    // @GetMapping("/kontakt")
    // public String contact() {
    //     return "redirect:/contact";
    // }
}