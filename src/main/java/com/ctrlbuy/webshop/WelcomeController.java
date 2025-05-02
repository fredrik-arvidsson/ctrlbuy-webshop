package com.ctrlbuy.webshop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/welcome")
    public String showWelcomePage() {
        return "welcome";  // pekar på templates/welcome.html
    }
}
