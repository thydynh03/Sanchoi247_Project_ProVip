package com.example.SanChoi247.service;
// package com.example.SanChoi247.model.service;

// import java.time.LocalDateTime;
// import com.example.SanChoi247.model.dto.UserSecretInfo;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import com.example.SanChoi247.model.entity.User;
// import com.example.SanChoi247.model.repo.UserRepo;
// import com.example.SanChoi247.model.repo.UserSecretsRepo;

// @Service
// public class VerifyEmailService {
//     @Autowired
//     EmailService emailService;
//     @Autowired
//     double timeWindow;
//     @Autowired
//     UserRepo userRepo;
//     @Autowired
//     UserSecretsRepo userSecretsRepo;
//     @Autowired
//     OTPService otpService;

//     public VerifyEmailService(EmailService emailService,
//             @Value("${OTP_TIME_WINDOW_MINUTES}") double timeWindow,
//             UserRepo userRepo,
//             UserSecretsRepo userSecretsRepo,
//             OTPService otpService) {
//         this.emailService = emailService;
//         this.timeWindow = timeWindow;
//         this.userRepo = userRepo;
//         this.userSecretsRepo = userSecretsRepo;
//         this.otpService = otpService;
//     }

//     public boolean isUnverified(String email) throws Exception {
//         User user = userRepo.getUserByEmail(email);
//         user = user == null ? userRepo.getUserByUsername(email) : user;
//         if (user == null) {
//             throw new Exception("User not exist");
//         }
//         return !user.isVerified();
//     }

//     public String sendOTP(String email) throws Exception {
//         User user = userRepo.getUserByEmail(email);
//         user = user == null ? userRepo.getUserByUsername(email) : user;
//         if (user == null) {
//             throw new Exception("User with email " + email + "not exist.");
//         }

//         // Check if the user has a secret key
//         UserSecretInfo secretKey = userSecretsRepo.findByUserId(user.getUid());
//         if (secretKey == null) {
//             // If the user doesn't have a secret key, generate a new one
//             secretKey = new UserSecretInfo();
//             secretKey.setUid(user.getUid());
//             secretKey.setSecretKey(SecretGenerator.generateSecret());
//             userSecretsRepo.insertSecretKey(user.getUid(), secretKey.getSecretKey());
//         } else if (secretKey.getCreatedAt().plusMinutes((int) timeWindow).isAfter(LocalDateTime.now())) {
//             throw new Exception("An email with OTP still valid was sent not long ago. Please try again later.");

//         }

//         // Generate an OTP for the user
//         String otp = otpService.generateOTP(secretKey.getSecretKey());

//         // Send the OTP to the user's email address
//         emailService.sendOTP(user.getEmail(), user.getUsername(), otp);

//         return "Email sent to " + email.replaceAll("(?<=.).(?=[^@]*?.@)", "*");
//     }

//     public boolean verifyOTP(String email, String userOTP) throws Exception {
//         // Check if the user already exists in the database
//         User user = userRepo.getUserByEmail(email);
//         user = user == null ? userRepo.getUserByUsername(email) : user;
//         if (user == null) {
//             throw new Exception("User not found");
//         }

//         // Check if the user has a secret key
//         String secretKey = userSecretsRepo.getSecretKey(user.getUid());
//         if (secretKey == null) {
//             throw new Exception("Secret key not found");
//         }

//         // Verify the OTP
//         if (!otpService.validateOTP(secretKey, userOTP)) {
//             throw new Exception("Invalid OTP");
//         }

//         user.setRole(Character.toLowerCase(user.getRole()));
//         userRepo.save(user);
//         return true;
//     }
// }
