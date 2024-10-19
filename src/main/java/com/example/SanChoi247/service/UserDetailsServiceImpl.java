package com.example.SanChoi247.service;
// package com.example.SanChoi247.model.service;

// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.example.SanChoi247.model.entity.User;
// import com.example.SanChoi247.model.repo.UserRepo;

// import lombok.SneakyThrows;

// @Service
// public class UserDetailsServiceImpl implements UserDetailsService {
//     @Autowired
//     UserRepo userRepo;

//     @Autowired
//     public UserDetailsServiceImpl(UserRepo userRepo) {
//         this.userRepo = userRepo;
//     }

//     @SneakyThrows
//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         User user = userRepo.getUserByUsername(username);
//         if (user == null) {
//             throw new UsernameNotFoundException("Could not find user");
//         }

//         // Get the role of the user
//         String role = user.getUserRole().toString();

//         // Create a list of authorities
//         List<GrantedAuthority> authorities = new ArrayList<>();
//         authorities.add(new SimpleGrantedAuthority(role));

//         return new org.springframework.security.core.userdetails.User(user.getUsername(),
//                 user.getPassword(), authorities);
//     }
// }
