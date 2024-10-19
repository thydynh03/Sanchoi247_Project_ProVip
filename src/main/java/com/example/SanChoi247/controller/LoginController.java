package com.example.SanChoi247.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.SanChoi247.model.dto.SignUpRequest;
import com.example.SanChoi247.model.entity.Post;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.LoginRepo;
import com.example.SanChoi247.model.repo.UserRepo;
import com.example.SanChoi247.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    PostService postService;

    @GetMapping("/")
    public String ShowIndex(Model model, HttpServletRequest httpServletRequest) throws Exception {
        ArrayList<User> sanList = userRepo.getAllUser();
        ArrayList<User> Owner = new ArrayList<>();
        for (User user : sanList) {
            if (user.getRole() == 'C') {
                Owner.add(user);
            }
        }
        model.addAttribute("userList", Owner);
        return "public/index";
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
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        } else {
            httpSession.setAttribute("UserAfterLogin", user);
            return "redirect:/";
        }
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
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setRole(Character.toUpperCase(role));
        userRepo.saveNew(newUser);

        if (role == 'C') {
            userRepo.saveOnSignup(newUser);
        }
        httpSession.setAttribute("UserAfterLogin", newUser);
        return "redirect:/auth/verifyEmail";
    }

    /*----------------------------blog----------------------- */
    @GetMapping("/blog")
    public String showCreatePostForm(Model model) {
        // Tạo đối tượng Post mới để binding với form
        model.addAttribute("post", new Post());

        // Lấy danh sách các bài viết đã có để hiển thị
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        return "user/createPost"; // Trả về trang createPost.html
    }

    @PostMapping("/posts")
public String createPost(@ModelAttribute Post post, HttpSession session, Model model) {
    User currentUser = (User) session.getAttribute("UserAfterLogin");

    // Kiểm tra xem người dùng đã đăng nhập chưa
    if (currentUser == null) {
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
    }

    // Thiết lập tên tác giả (author_name) và id của tác giả (author_id)
    post.setAuthorId(currentUser.getUid());
    post.setAuthorName(currentUser.getName());

    // Lưu bài viết mới vào cơ sở dữ liệu
    postService.save(post);

    // Sau khi lưu, cập nhật danh sách các bài viết và trả về cùng trang
    List<Post> posts = postService.getAllPosts();
    model.addAttribute("posts", posts);
    model.addAttribute("post", new Post());

    // Trả về template createPost.html cùng dữ liệu đã được cập nhật
    return "user/createPost";
}


    
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam("postId") Long postId, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("UserAfterLogin");
    
        // Kiểm tra xem người dùng có quyền xóa bài viết không
        Post post = postService.getPostById(postId);
        if (currentUser == null || (post.getAuthorId() != currentUser.getUid() && currentUser.getRole() != 'A')) {
            return "redirect:/error"; // Trả về trang lỗi nếu người dùng không có quyền
        }
    
        postService.delete(postId); // Xóa bài viết
    
        // Cập nhật lại danh sách các bài viết và trả về cùng trang
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
    
        return "user/createPost";
    }
    
    
}
