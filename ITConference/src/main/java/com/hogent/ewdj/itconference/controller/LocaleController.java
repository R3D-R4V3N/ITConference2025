package com.hogent.ewdj.itconference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
public class LocaleController {

    @Autowired
    private LocaleResolver localeResolver;

    @GetMapping("/changeLocale")
    public String changeLocale(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("lang") String lang) {
        Locale locale = switch (lang) {
            case "nl" -> new Locale("nl");
            case "en" -> Locale.ENGLISH;
            default -> Locale.ENGLISH;
        };

        localeResolver.setLocale(request, response, locale);

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/events";
        }
    }
}