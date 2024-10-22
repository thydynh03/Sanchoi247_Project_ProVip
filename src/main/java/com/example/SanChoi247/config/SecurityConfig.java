// package com.example.SanChoi247.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.ObjectPostProcessor;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import com.example.SanChoi247.model.repo.UserRepo;
// import com.example.SanChoi247.model.service.UserDetailsServiceImpl;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     UserRepo userRepo;
//     @Autowired
//     ObjectPostProcessor<Object> objectPostProcessor;

//     @Autowired
//     public SecurityConfig(UserRepo userRepo, ObjectPostProcessor<Object> objectPostProcessor) {
//         this.userRepo = userRepo;
//         this.objectPostProcessor = objectPostProcessor;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder(13);
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(HttpSecurity http,
//             UserDetailsService userDetailService)
//             throws Exception {
//         return http.getSharedObject(AuthenticationManagerBuilder.class)
//                 .userDetailsService(userDetailService)
//                 .passwordEncoder(passwordEncoder())
//                 .and()
//                 .build();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(new UserDetailsServiceImpl(userRepo));
//         authProvider.setPasswordEncoder(passwordEncoder());
//         return authProvider;
//     }

//     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//         auth.authenticationProvider(authenticationProvider());
//     }

//     @Bean
//     public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
//         http.securityMatcher("/api/v1/owner/**")
//                 .authorizeHttpRequests(authorize -> authorize.anyRequest().hasAuthority("OWNER"))
//                 .addFilterBefore(jwtRequestFilter,
//                         UsernamePasswordAuthenticationFilter.class)
//                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .csrf(AbstractHttpConfigurer::disable);
//         return http.build();
//     }
// }
