package com.example.SanChoi247.service;
// package com.example.SanChoi247.model.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.server.ResponseStatusException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.beans.factory.annotation.Value;

// import com.example.SanChoi247.config.JwtUtil;
// import com.example.SanChoi247.model.dto.UserSecretInfo;
// import com.example.SanChoi247.model.entity.User;
// import com.example.SanChoi247.model.repo.UserRepo;
// import com.example.SanChoi247.model.repo.UserSecretsRepo;

// public class PasswordResetService {
//     @Autowired
//     PasswordEncoder passwordEncoder;
//     @Autowired
//     UserRepo userRepo;
//     @Autowired
//     UserSecretsRepo userSecretsRepo;
//     @Autowired
//     OTPService otpService;
//     @Autowired
//     EmailService emailService;
//     @Autowired
//     int OTP_TIME_WINDOW_MINUTES;
//     @Autowired
//     JwtUtil jwtUtil;
//     @Autowired
//     UserDetailsServiceImpl userDetailsServiceImpl;

//     public PasswordResetService(PasswordEncoder passwordEncoder, UserRepo userRepo,
//             UserSecretsRepo userSecretsRepo, OTPService otpService, EmailService emailService,
//             @Value("${OTP_TIME_WINDOW_MINUTES}") int OTP_TIME_WINDOW_MINUTES, JwtUtil jwtUtil,
//             UserDetailsServiceImpl userDetailsServiceImpl) {
//         this.passwordEncoder = passwordEncoder;
//         this.userRepo = userRepo;
//         this.userSecretsRepo = userSecretsRepo;
//         this.otpService = otpService;
//         this.emailService = emailService;
//         this.OTP_TIME_WINDOW_MINUTES = OTP_TIME_WINDOW_MINUTES;
//         this.jwtUtil = jwtUtil;
//         this.userDetailsServiceImpl = userDetailsServiceImpl;
//     }

//     public String handleFormSubmission(String email) throws Exception {
//         // Check if the user already exists in the database
//         User user = userRepo.getUserByEmail(email);
//         user = user == null ? userRepo.getUserByUsername(email) : user;
//         if (user == null) {
//             // If the user doesn't exist, create a new user
//             user = new User();
//             user.setEmail(email);
//             userRepo.save(user);
//         }

//         email = user.getEmail();
//         String censored_email = email.replaceAll("(^.).+(.)(?=@)", "$1********$2").replaceAll("(@.).+(.)\\.",
//                 "$1****$2.");

//         // Generate a secret key for the user
//         UserSecretInfo secretKey = userSecretsRepo.findByUserId(user.getUid());
//         if (secretKey != null) {
//             if (secretKey.getCreatedAt() != null && secretKey.getCreatedAt().plusMinutes(OTP_TIME_WINDOW_MINUTES)
//                     .isAfter(java.time.LocalDateTime.now())) {
//                 return "OTP already sent to " + censored_email + ". Please check your inbox.";
//             }
//             userSecretsRepo.deleteSecretKey(user.getUid());
//         }
//         String secretString = SecretGenerator.generateSecret();
//         userSecretsRepo.insertSecretKey(user.getUid(), secretString);

//         // Generate an OTP for the user
//         String otp = otpService.generateOTP(secretString);

//         // Send the OTP to the user's email address
//         emailService.sendOTP(user.getEmail(), user.getUsername(), otp);

//         return "OTP sent to " + censored_email;
//     }

//     public String verifyOTP(String email, String otp) throws Exception {
//         // Check if the user already exists in the database
//         User user = userRepo.getUserByEmail(email);
//         user = user == null ? userRepo.getUserByUsername(email) : user;
//         if (user == null) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
//         }

//         // Check if the user has a secret key
//         String secretKey = userSecretsRepo.getSecretKey(user.getUid());
//         if (secretKey == null) {
//             ;
//             throw new RuntimeException("Secret key not found");
//         }

//         // Verify the OTP
//         if (!otpService.validateOTP(secretKey, otp)) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
//         }
//         userSecretsRepo.deleteSecretKey(user.getUid());

//         // Generate a reset token for the user and return it
//         return jwtUtil.generateToken(userDetailsServiceImpl.loadUserByUsername(user.getUsername()));
//     }

//     public void resetPassword(String token, String newPassword) throws Exception {
//         // Verify the token
//         String userName = jwtUtil.extractUsername(token);
//         if (userName == null) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token.");
//         }

//         // Find the user
//         User user = userRepo.getUserByUsername(userName);
//         if (user == null) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found from token.");
//         }

//         // Check password strength
//         if (!userRepo.isValidPassword(newPassword)) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password not strong enough lah.");
//         }

//         // Update the user's password
//         user.setPassword(passwordEncoder.encode(newPassword));
//         userRepo.save(user);
//     }
// }
