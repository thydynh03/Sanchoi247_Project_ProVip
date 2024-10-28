// package com.example.SanChoi247.config;

// import java.io.IOException;

// import org.springframework.beans.factory.annotation.Autowired;
// import
// org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import
// org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import io.jsonwebtoken.ExpiredJwtException;

// import com.example.SanChoi247.model.service.UserDetailsServiceImpl;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class JwtRequestFilter extends OncePerRequestFilter{
// @Autowired
// UserDetailsServiceImpl userDetailsService;
// @Autowired
// JwtUtil jwtUtil;
// @Autowired
// public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil
// jwtUtil) {
// this.userDetailsService = userDetailsService;
// this.jwtUtil = jwtUtil;
// }

// @Override
// protected void doFilterInternal(HttpServletRequest request,
// HttpServletResponse response, FilterChain chain)
// throws ServletException, IOException {

// final String requestTokenHeader = request.getHeader("Authorization");

// String username = null;
// String jwtToken = null;

// if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
// jwtToken = requestTokenHeader.substring(7);
// try {
// username = jwtUtil.extractUsername(jwtToken);
// } catch (IllegalArgumentException e) {
// System.out.println("Unable to get JWT Token");
// } catch (ExpiredJwtException e) {
// System.out.println("JWT Token has expired");
// }
// }

// if (username != null &&
// SecurityContextHolder.getContext().getAuthentication() == null) {

// UserDetails userDetails =
// this.userDetailsService.loadUserByUsername(username);

// if (jwtUtil.validateToken(jwtToken, userDetails)) {
// UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
// UsernamePasswordAuthenticationToken(
// userDetails, null, userDetails.getAuthorities());
// usernamePasswordAuthenticationToken
// .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
// SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
// }
// }
// chain.doFilter(request, response);
// }
// }
