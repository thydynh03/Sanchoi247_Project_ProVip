package com.example.SanChoi247.controller;

import java.sql.Date;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.SanChoi247.model.dto.SignUpRequest;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.LoginRepo;
import com.example.SanChoi247.model.repo.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String ShowIndex(Model model, HttpServletRequest httpServletRequest) throws Exception {
        ArrayList<User> sanList = userRepo.getAllUser();
        ArrayList<User> Owner = new ArrayList<>();
        for (User user : sanList) {
            if (user.getRole() == 'C' && user.getStatus() == 4) {
                Owner.add(user);
            }
        }
        model.addAttribute("userList", Owner);
        return "public/index";// + httpServletRequest.getSession().getId()
    }

    @GetMapping("/Login")
    public String showLogin(Model model) throws Exception {
        return "auth/login";
    }

    @GetMapping("/Logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("UserAfterLogin");
        httpSession.invalidate();
        return "redirect:/";
    }

    @PostMapping("/LoginToSystem")
    public String LoginToSystem(@RequestParam("username") String username, @RequestParam("password") String password,
            HttpSession httpSession, Model model) throws Exception {
        User user = loginRepo.checkLogin(username, password);

        if (user == null) {
            model.addAttribute("error", "Invalid login, please try again");
            return "auth/login";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Invalid login, please try again");
            return "auth/login";
        }

        httpSession.setAttribute("UserAfterLogin", user);

        String redirect = (String) httpSession.getAttribute("redirect");
        if (redirect != null) {
            httpSession.removeAttribute("redirect");
            return "redirect:" + redirect;
        }
        if (user.getRole() == 'A') {
            return "redirect:/admin/dashboard";
        }
        if (user.getRole() == 'p' || user.getRole() == 'b') {
            return "auth/banned";
        }
        
        return "redirect:/";
    }

    // ---------------------------------------------------------------------------//

    @GetMapping("/ShowSignup")
    public String showSignup(Model model) {
        return "auth/signup";
    }

    @PostMapping("/Signup")
    public String signUp(@RequestBody SignUpRequest signUpRequest, Model model,
            HttpSession httpSession)
            throws Exception {
        String name = signUpRequest.getName();
        String email = signUpRequest.getEmail();
        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        char role = signUpRequest.getRole();

        if (userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }
        if (!userRepo.isValidPassword(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters long and include at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.");
        }
        // String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setUsername(username);
        // newUser.setPassword(hashedPassword);
        newUser.setRole(Character.toUpperCase(role));
        userRepo.saveNew(newUser);

        if (role == 'C') {
            userRepo.saveOnSignup(newUser);
        }
        httpSession.setAttribute("UserAfterLogin", newUser);
        // verifyEmailService.sendOTP(email);
        return "redirect:/auth/verifyEmail";
    }
}