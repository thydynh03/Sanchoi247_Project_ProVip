// package com.example.SanChoi247.config;

// import java.io.IOException;

// import org.springframework.stereotype.Component;

// import com.example.SanChoi247.model.entity.User;

// import jakarta.servlet.Filter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;

// @Component
// public class AuthFilter implements Filter {

// public void doFilter(ServletRequest request, ServletResponse response,
// FilterChain chain)
// throws IOException, ServletException {
// HttpServletRequest httpServletRequest = (HttpServletRequest) request;
// HttpServletResponse httpServletResponse = (HttpServletResponse) response;
// String uri = httpServletRequest.getRequestURI();
// HttpSession session = (HttpSession) httpServletRequest.getSession();
// User user = (User) session.getAttribute("UserAfterLogin");
// // AP DUNG CHO TAT CA CAC ROLE
// if ((uri.equals("/Login") || uri.equals("/") || uri.equals("/LoginToSystem")
// || uri.equals("/Logout")
// || uri.equals("/ShowIntroduction") || uri.equals("/ShowForOwners") ||
// uri.startsWith("/ViewDetail/")
// || uri.startsWith("/ShowDetailLocation/") || uri.equals("/ShowSearch"))) {
// chain.doFilter(httpServletRequest, httpServletResponse);
// return;
// }

// if (user == null) {
// httpServletResponse.sendRedirect("/Login");
// } else if (user.getRole() == 'C' && uri.equals("/ShowAddProduct")) {// AP
// DUNG CHO SELLER
// chain.doFilter(httpServletRequest, httpServletResponse);
// } else if (user.getRole().equals("U") && uri.equals("/OrderProduct") ||
// uri.startsWith("/ShowOrder/")
// || uri.equals("/ShowOrderByUserId") || uri.equals("/ShowCart") ||
// uri.startsWith("/AddToCart")
// || uri.startsWith("/Reduce/") || uri.startsWith("/Increase") ||
// uri.equals("/BuyProductInCart")) {
// } else if (user.getRole().equals("A")) {
// chain.doFilter(httpServletRequest, httpServletResponse);
// }

// else {
// httpServletResponse.sendRedirect("/");
// }
// }
// }
