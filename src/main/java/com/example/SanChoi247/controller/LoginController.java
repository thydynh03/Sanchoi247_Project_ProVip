
package com.example.SanChoi247.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.example.SanChoi247.model.dto.SignUpRequest;
import com.example.SanChoi247.model.entity.Comment;
import com.example.SanChoi247.model.entity.Post;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.LoginRepo;
import com.example.SanChoi247.model.repo.UserRepo;
import com.example.SanChoi247.security.JwtTokenProvider;
import com.example.SanChoi247.service.PostService;
import com.example.SanChoi247.service.SendOtpToMailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@Controller
public class LoginController {
        @Autowired
    PostService postService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    LoginRepo loginRepo;
    @Autowired

    private SendOtpToMailService sendOtpToMailService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Inject JwtTokenProvider

    private String generatedOtp;  // Biến để lưu OTP
    private String email; // Lưu email để sử dụng cho gửi lại OT

    
    
    
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
    public String showLogin(Model model) {
        return "auth/login";
    }

    @GetMapping("/Logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("UserAfterLogin");
        httpSession.invalidate();
        return "redirect:/";
    }

    @PostMapping("/LoginToSystem")
    public ResponseEntity<?> loginToSystem(@RequestParam("username") String username, 
                                            @RequestParam("password") String password,
                                            HttpSession httpSession) throws Exception {
        User user = loginRepo.checkLogin(username, password);
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password")); // Trả về thông báo lỗi
        } else {
            // Tạo JWT token
            String token = jwtTokenProvider.generateToken(user);
            
            // Thông báo trong console
            System.out.println("Token created for user: " + username + " - Token: " + token);
            System.out.println("Token created for user: " + user.getUid() + " - Token: " + token);
            System.out.println("Token created for user: " + user.getAvatar() + " - Token: " + token);
            System.out.println("Token created for user: " + user.getName() + " - Token: " + token);

            // Lưu token vào session
            httpSession.setAttribute("accessToken", token);
            httpSession.setAttribute("UserAfterLogin", user); // Lưu thông tin người dùng vào session
            httpSession.setAttribute("userRole", user.getRole()); // Lưu role vào session riêng biệt


            // Trả về token và thông tin cần thiết
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", token);
            response.put("username", user.getUsername());
            response.put("uid", user.getUid()); // Thêm UID vào phản hồi
            response.put("avatar", user.getAvatar()); // Thêm avatar vào phản hồi
            response.put("name", user.getName()); // Thêm name vào phản hồi
            
            return ResponseEntity.ok(response); // Trả về phản hồi thành công
        }
    }
    @PostMapping("/UserAfterLogin")
    @ResponseBody
    public ResponseEntity<?> afterLoginWithGG(HttpServletRequest request, Authentication authentication) throws Exception {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    
        HttpSession session = request.getSession();
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
    
        // Lấy thông tin người dùng từ Google
        String email = (String) oauthUser.getAttribute("email");
        String name = (String) oauthUser.getAttribute("name");
        String avatar = (String) oauthUser.getAttribute("picture");
    
        // Kiểm tra người dùng có tồn tại trong cơ sở dữ liệu không
        User existingUser = userRepo.getUserByEmail(email);
    
        if (existingUser == null) {
            User newUser = createUser(email, name, avatar);
            userRepo.addNewUser(newUser);
            existingUser = newUser;
        } else {
            existingUser.setAvatar(avatar);
            userRepo.save(existingUser);
        }
    
        int uid = existingUser.getUid();
        String token = jwtTokenProvider.generateToken(existingUser);
    
        // Lưu thông tin vào session
        session.setAttribute("UserAfterLogin", existingUser);
        session.setAttribute("accessToken", token);
        session.setAttribute("userRole", existingUser.getRole()); // Lưu role vào session riêng biệt

        // Tạo đối tượng phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("username", existingUser.getUsername());
        response.put("uid", uid);
        response.put("avatar", avatar);
        response.put("name", name);
    
        return ResponseEntity.ok(response); // Trả về JSON thành công
    }
    
    
    

private User createUser(String email, String name, String avatar) {
    User newUser = new User();
    newUser.setEmail(email); // Lưu email vào cột email
    newUser.setUsername(email); // Sử dụng email làm tên người dùng
    newUser.setName(name); // Lưu tên
    newUser.setAvatar(avatar); // Lưu avatar
    newUser.setPassword(new BCryptPasswordEncoder().encode("defaultPassword")); // Mật khẩu mặc định
    newUser.setStatus(0); // Trạng thái mặc định
    newUser.setRole('U'); // Vai trò mặc định là người dùng
    return newUser;
}


@GetMapping("/auth/login")
public String showLoginPage() {
    return "auth/login"; // Trả về tên view trong thư mục templates
}
    
    
    @GetMapping("/Signup")
    public String showSignUpForm() {
        return "auth/login"; // Trả về trang đăng ký
    }

    @PostMapping("/Signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest, HttpSession httpSession) throws Exception {
        String email = signUpRequest.getEmail();
        String name = signUpRequest.getName();
        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        String confirmPassword = signUpRequest.getConfirmPassword();
        char role = 'U'; // Default role
        char gender = signUpRequest.getGender(); // Get first character (M or F)
        
    
    // Check for existing username
    if (userRepo.existsByUsername(username)) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Username already exists."));
    }

    // Check for existing email
    if (userRepo.existsByEmail(email)) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Email already exists."));
    }

    // Validate password
    if (!userRepo.isValidPassword(password)) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Password invalid."));
    }

// Check if passwords match
System.out.println("Password: " + password);
System.out.println("Confirm Password: " + confirmPassword);
    if (!userRepo.isValidPassword(password)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password invalid.");
    }


    User newUser = new User();
    newUser.setName(name);
    newUser.setEmail(email);
    newUser.setUsername(username);
    newUser.setPassword(password);
    newUser.setRole(Character.toUpperCase(role));
    newUser.setGender(gender); // Store the gender
    // Store user in session
    System.out.println("New User before saving: " + newUser);
    
    // Validate newUser fields
    if (newUser.getName() == null || newUser.getEmail() == null || newUser.getUsername() == null || newUser.getPassword() == null) {
        throw new Exception("Name, Email, or Username cannot be null.");
    }

    // Store user in session
    httpSession.setAttribute("newUser", newUser);


    // Send OTP via email
    try {
        generatedOtp = sendOtpToMailService.sendOtpService(email);
        return ResponseEntity.ok(new SuccessResponse("OTP sent to your email.", "auth/enterOtp"));
        
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}

// ErrorResponse and SuccessResponse classes for structured responses
public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

public class SuccessResponse {
    private String message;
    private String redirectUrl;

    public SuccessResponse(String message) {
        this.message = message;
    }

    public SuccessResponse(String message, String redirectUrl) {
        this.message = message;
        this.redirectUrl = redirectUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}

// Check password match in userRepo
public boolean isPasswordMatching(SignUpRequest signUpRequest) {
    String password = signUpRequest.getPassword();
    String confirmPassword = signUpRequest.getConfirmPassword();
    return password != null && password.equals(confirmPassword);
}

    

    @PostMapping("/resendOtp")
    public String resendOtp(Model model) {
        try {
            generatedOtp = sendOtpToMailService.sendOtpService(email); // Gửi lại OTP
            model.addAttribute("success", "OTP đã được gửi lại.");
            model.addAttribute("email", email);
            return "auth/enterOtp"; // Chuyển hướng đến trang nhập OTP
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login"; // Quay lại trang nhập OTP
        }
    }
    @GetMapping("/auth/enterOtp")
public String enterOtpPage() {
    return "auth/enterOtp"; // This should point to your template/view file
}


/*---------------------BLOG--------------------- */
@GetMapping("/blog")
    public String showCreatePostForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("UserAfterLogin");
        if (currentUser != null) {
            model.addAttribute("userId", currentUser.getUid()); // Thêm userId vào model
            model.addAttribute("userRole", currentUser.getRole()); // Truyền userRole vào model
        }
        // Tạo đối tượng Post mới để binding với form
        model.addAttribute("post", new Post());

        // Lấy danh sách các bài viết đã có cùng với bình luận để hiển thị
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        return "user/createPost"; // Trả về trang createPost.html
    }

    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("UserAfterLogin");
    
        if (currentUser == null) {
            return "redirect:/login";
        }
    
        post.setAuthorId(currentUser.getUid());
        post.setAuthorName(currentUser.getName());
        postService.save(post);
    
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("userId", currentUser.getUid());
        model.addAttribute("userRole", currentUser.getRole());  // Thêm userRole vào model
    
        return "user/createPost"; 
    }
    
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam("postId") Long postId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("UserAfterLogin");
    
        if (currentUser == null) {
            return "redirect:/login";
        }
    
        Post post = postService.getPostById(postId);
        if (post == null || (currentUser.getRole() != 'A' && post.getAuthorId() != currentUser.getUid())) {
            return "redirect:/error";
        }
    
        postService.delete(postId);
    
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("userId", currentUser.getUid());
        model.addAttribute("userRole", currentUser.getRole());  // Truyền userId vào model
    
        return "user/createPost"; 
    }
    

    @PostMapping("/comments")
    public String addComment(@RequestParam("postId") Long postId, @RequestParam("content") String content, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("UserAfterLogin");

        // Tạo và lưu bình luận mới
        Comment comment = new Comment();
        comment.setAuthorId(currentUser.getUid());
        comment.setAuthorName(currentUser.getName());
        comment.setContent(content);
        comment.setPost(new Post(postId)); // Gán bình luận cho bài viết

        postService.saveComment(comment);

        // Lấy lại danh sách bài viết và các bình luận sau khi thêm bình luận
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        return "redirect:/blog"; // Chuyển hướng về trang blog
    }
    @PostMapping("/comments/edit")
    public ResponseEntity<?> editComment(@RequestParam("commentId") Long commentId, 
                                        @RequestParam("content") String content, 
                                        HttpSession session) {
    User currentUser = (User) session.getAttribute("UserAfterLogin");

    if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    Comment comment = postService.getCommentById(commentId);
    if (comment.getAuthorId() != currentUser.getUid()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to edit this comment");
    }

    comment.setContent(content);
    postService.saveComment(comment);

    return ResponseEntity.ok("Comment edited successfully");
}

@PostMapping("/comments/delete")
public ResponseEntity<?> deleteComment(@RequestParam("commentId") Long commentId, HttpSession session) {
    User currentUser = (User) session.getAttribute("UserAfterLogin");

    if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    Comment comment = postService.getCommentById(commentId);

    // Cho phép admin hoặc tác giả của bình luận xóa bình luận
    if (currentUser.getRole() == 'A' || comment.getAuthorId() == currentUser.getUid()) {
        postService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to delete this comment");
    }
}
  
/*---------------------BLOG--------------------- */


@PostMapping("/verifyOtp")
public String verifyOtp(@RequestParam("otp") String otp, HttpSession httpSession, Model model) throws Exception {
    User newUser = (User) httpSession.getAttribute("newUser"); // Lấy thông tin người dùng từ session
    if (newUser == null) {
        model.addAttribute("error", "user null");
        return "auth/enterOtp";
    }
    
    // Kiểm tra OTP
    if (otp == null || otp.trim().isEmpty()) {
        model.addAttribute("error", "OTP không được để trống");
        return "auth/enterOtp";
    }

    if (!generatedOtp.equals(otp)) {
        model.addAttribute("error", "OTP không chính xác");
        return "auth/enterOtp";
    }

    if (otp.equals(generatedOtp)) {
        // Lưu người dùng vào database
        System.out.println("Saving user to database: " + newUser);
        try {
            userRepo.addNewUser(newUser);
            System.out.println("User saved successfully: " + newUser);
            httpSession.invalidate(); // Hủy session sau khi đăng ký thành công
            return "auth/login"; // Chuyển đến trang đăng nhập sau khi đăng ký thành công
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error saving user: " + e.getMessage());
            return "auth/enterOtp"; // Trở lại trang nếu có lỗi
        }

        // userRepo.saveOnSignup(newUser); // Bỏ dòng này nếu không cần

        // Xóa thông tin trong session sau khi hoàn thành đăng ký

    } else {
        model.addAttribute("error", "Invalid OTP. Please try again.");
        return "auth/enterOtp"; // OTP sai thì trở lại trang nhập OTP
    }
}


}
