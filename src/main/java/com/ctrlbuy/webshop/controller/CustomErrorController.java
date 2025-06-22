package com.ctrlbuy.webshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Hämta feldetaljer
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        // Logga detaljerad felinformation
        log.error("Error details - Status: {}, Exception: {}, Message: {}, URI: {}",
                status, exception, message, requestUri);

        if (exception != null) {
            log.error("Exception details: ", (Throwable) exception);
        }

        // Lägg till felinformation i modellen
        model.addAttribute("status", status != null ? status : "Unknown");
        model.addAttribute("error", exception != null ? exception.toString() : "Unknown error");
        model.addAttribute("message", message != null ? message : "No error message available");
        model.addAttribute("path", requestUri != null ? requestUri : "Unknown path");

        // Om felet är relaterat till inloggning
        String uri = requestUri != null ? requestUri.toString() : "";
        if (uri.contains("/login-process")) {
            log.error("Login process error detected");
            return "redirect:/login?methodNotAllowed=true";
        }

        // Skapa en enkel HTML-svar
        if (!model.containsAttribute("isAuthenticated")) {
            model.addAttribute("isAuthenticated", false);
        }

        return "simple-error";
    }
}