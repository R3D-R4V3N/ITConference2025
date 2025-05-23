package com.hogent.ewdj.itconference.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (error != null) {
            String errorMessage = messageSource.getMessage(
                    "login.error.invalidCredentials",
                    null,
                    LocaleContextHolder.getLocale()
            );
            model.addAttribute("error", errorMessage);
        }

        if (logout != null) {
            String logoutMessage = messageSource.getMessage(
                    "login.logout.success",
                    null,
                    LocaleContextHolder.getLocale()
            );
            model.addAttribute("msg", logoutMessage);
        }

        return "login";
    }
}