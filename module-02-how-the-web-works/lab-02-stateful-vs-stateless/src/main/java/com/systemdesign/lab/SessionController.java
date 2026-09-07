package com.systemdesign.lab;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    @PostMapping("/login")
    public String login(@RequestParam String username, HttpSession session) {
        session.setAttribute("username", username);
        session.setAttribute("cart", new ArrayList<String>());
        return "Logged in as:" + username + " | Session ID:" + session.getId();
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam String item, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "ERROR: Not logged in. No Session found.";
        }

        List<String> cart = (List<String>) session.getAttribute("cart");
        cart.add(item);
        session.setAttribute("cart", cart);
        return username + "'s cart: " + cart;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "ERROR: Not logged in. No Session found.";
        }

        List<String> cart = (ArrayList<String>) session.getAttribute("cart");
        return username + "'s cart: " + cart + " | Session ID:" + session.getId();
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out. Session destroyed";
    }

}
