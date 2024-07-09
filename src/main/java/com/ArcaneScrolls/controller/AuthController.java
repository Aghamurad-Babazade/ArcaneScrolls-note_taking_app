package com.ArcaneScrolls.controller;

import com.ArcaneScrolls.model.User;
import com.ArcaneScrolls.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam String username, @RequestParam String password, @RequestParam String email) throws Exception {

        userService.save(username, password, email);
        return "redirect:/login";

    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username_or_email") String username_or_email, @RequestParam("password") String password, Model model, HttpSession session) {

        User existingUser = userService.findByUsernameAndPassword(username_or_email, password);

        if (existingUser == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        } else {
            session.setAttribute("user", existingUser);
            return "redirect:/notes";
        }
    }


}
