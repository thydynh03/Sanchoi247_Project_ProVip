package com.example.SanChoi247.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.dto.OwnerStatistics;
import com.example.SanChoi247.model.dto.TableOwnerStatistics;
import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.entity.Booking.PaymentStatus;
import com.example.SanChoi247.model.repo.BookingRepo;
import com.example.SanChoi247.model.repo.OwnerRepo;
import com.example.SanChoi247.model.repo.UserRepo;
import com.example.SanChoi247.model.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OwnerController {
    @Autowired
    OwnerRepo ownerRepo;

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    UserRepo userRepo;
    @Autowired
    EmailService emailService;

    @GetMapping(value = "owner/dashboard")
    public String ownerDashboardPage(Model model, HttpSession httpSession) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        OwnerStatistics statistics = ownerRepo.getStatisticsForOwner(user.getUid());
        List<TableOwnerStatistics> stadiumRevenues = ownerRepo.getFieldRevenues(user.getUid()); // Lấy doanh thu theo
                                                                                                // UID
        // List<TableOwnerStatistics> sortedStadiumRevenues = stadiumRevenues.stream()
        // .sorted(Comparator.comparingDouble(TableOwnerStatistics::getRevenue).reversed())
        // .collect(Collectors.toList());
        List<Double> monthlyRevenue = ownerRepo.getMonthlyRevenueForOwner(user.getUid());
        List<Double> dailyRevenue = ownerRepo.getDailyRevenueForOwner(user.getUid());

        model.addAttribute("statisticsOfOwner", statistics);
        model.addAttribute("stadiumRevenues", stadiumRevenues);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("dailyRevenue", dailyRevenue);
        return "owner/ownerDashboard";
    }

    @GetMapping(value = ("/ViewOwnerRefundRequests"))
    public String viewOwnerRefundRequests(Model model, HttpSession httpSession) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        ArrayList<Booking> request = ownerRepo.getAllRequestRefund(user.getUid());
        model.addAttribute("status", "reject");
        model.addAttribute("booking", request);
        return "owner/viewAllRequest";
    }

    @GetMapping("/ViewUserBooking")
    public String viewUserBooking(Model model, HttpSession httpSession) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        ArrayList<Booking> bookings = ownerRepo.getAllUserBooking(user.getUid());
        model.addAttribute("booking", bookings);
        return "owner/viewBookingOwner";
    }

    @GetMapping("/ViewTotalRefund")
    public String viewTotalRefund(Model model, HttpSession httpSession) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");

        // Lấy danh sách các booking của người dùng
        ArrayList<Booking> bookings = ownerRepo.getAllUserBooking(user.getUid());

        // Duyệt qua danh sách bookings để kiểm tra trạng thái thanh toán
        boolean hasPartialOrTotalRefund = bookings.stream().anyMatch(
                booking -> booking.getStatus() == PaymentStatus.PARTIALLY_REFUNDED ||
                        booking.getStatus() == PaymentStatus.TOTALLY_REFUNDED);

        // Thêm các thuộc tính vào model
        model.addAttribute("status", hasPartialOrTotalRefund);
        model.addAttribute("totalRefund", bookings);

        return "owner/viewTotalRefund";
    }

    @PostMapping(value = "/sendMailToViewBookingOwner")
    public String SendMailToViewOwnerRefundRequest(
            @RequestParam("Title") String title,
            @RequestParam("content") String content,
            @RequestParam("ownerEmails") List<String> ownerEmails,
            Model model) throws Exception {

        // Tạo tiêu đề email
        String subject = title;

        // Tạo nội dung email với các mục cần sửa
        String emailContent = "<html><body style='font-family: sans-serif;'>" +
                content
                +

                "<p style='font-size: 16px;'>Thank you for using SanChoi247!</p>" +
                "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                +
                "<br>" +
                "<p style='font-size: 12px; color: #555;'>(c) 2024 SanChoi247. All rights reserved</p>" +
                "</body></html>";

        // Gửi email thông báo tới từng người dùng
        for (String email : ownerEmails) {
            emailService.sendEmail(email, subject, emailContent);
        }
        return "redirect:/ViewUserBooking";
    }

    @PostMapping(value = "/sendMailToViewAllRequest")
    public String SendMailToViewAllRequest(
            @RequestParam("Title") String title,
            @RequestParam("content") String content,
            @RequestParam("ownerEmails") List<String> ownerEmails,
            Model model) throws Exception {

        // Tạo tiêu đề email
        String subject = title;

        // Tạo nội dung email với các mục cần sửa
        String emailContent = "<html><body style='font-family: sans-serif;'>" +
                content
                +

                "<p style='font-size: 16px;'>Thank you for using SanChoi247!</p>" +
                "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                +
                "<br>" +
                "<p style='font-size: 12px; color: #555;'>(c) 2024 SanChoi247. All rights reserved</p>" +
                "</body></html>";

        // Gửi email thông báo tới từng người dùng
        for (String email : ownerEmails) {
            emailService.sendEmail(email, subject, emailContent);
        }
        return "redirect:/ViewOwnerRefundRequests";
    }

    @PostMapping("/ApproveRequestOwner")
    public String approveRequestOwner(@RequestParam("booking_id") int bookingId, Model model) throws Exception {
        // Tìm kiếm booking bằng bookingId
        Booking booking = bookingRepo.findById(bookingId);

        if (booking == null) {
            model.addAttribute("error", "Booking not found.");
            return "error";
        }

        // Kiểm tra trạng thái hiện tại của booking
        if (booking.getStatus() == PaymentStatus.PENDING_TOTAL_REFUND
                || booking.getStatus() == PaymentStatus.PENDING_PARTIAL_REFUND) {
            // Cập nhật trạng thái thành PENDING_ADMIN_APPROVE_REFUND nếu là hoàn tiền
            booking.setStatus(PaymentStatus.PENDING_ADMINAPVORE_REFUND);
            bookingRepo.save(booking);

            // Lấy thông tin người dùng từ booking
            User user = userRepo.getUserById(booking.getUser().getUid());
            String subject = "Sanchoi247 Refund Request";
            String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>"
                    +
                    "<h1 style='color: #007bff; text-align: center;'>Sanchoi247</h1>" +
                    "<p style='font-size: 16px; color: #333;'>Hello <strong>" + user.getName() + "</strong>,</p>" +
                    "<p style='font-size: 16px; color: #333;'>Your refund request has been <strong style='color: #dc3545;'> approve</strong> by the admin.</p>"
                    +
                    "<p style='font-size: 16px; color: #333;'>If you have any questions, please contact our admin at <a href='mailto:247sanchoi@gmail.com' style='color: #007bff; text-decoration: none;'>247sanchoi@gmail.com</a> for further assistance.</p>"
                    +
                    "<p style='font-size: 16px; color: #333;'>Thank you for using Sanchoi247!</p>" +
                    "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                    +
                    "<br>" +
                    "<p style='font-size: 12px; color: #555; text-align: center;'>(c) 2024 Sanchoi247. All rights reserved</p>"
                    +
                    "</div>" +
                    "</body></html>";

            // Gửi email thông báo
            emailService.sendEmail(user.getEmail(), subject, content);

            model.addAttribute("message", "Refund request approved successfully. Email notification sent.");
        } else {
            model.addAttribute("error", "Booking is not eligible for refund approval.");
            return "error";
        }

        return "redirect:/ViewOwnerRefundRequests";
    }

    @PostMapping("/RejectRequestOwner")
    public String rejectRequestOwner(@RequestParam("booking_id") int bookingId, Model model) throws Exception {
        // Tìm kiếm booking bằng bookingId
        Booking booking = bookingRepo.findById(bookingId);

        if (booking == null) {
            model.addAttribute("error", "Booking not found.");
            return "error";
        }

        // Kiểm tra trạng thái hiện tại của booking
        if (booking.getStatus() == PaymentStatus.PENDING_TOTAL_REFUND
                || booking.getStatus() == PaymentStatus.PENDING_PARTIAL_REFUND) {
            // Cập nhật trạng thái thành FAILED để biểu thị bị từ chối
            booking.setStatus(PaymentStatus.FAILED);
            bookingRepo.save(booking);

            // Lấy thông tin người dùng từ booking
            User user = userRepo.getUserById(booking.getUser().getUid());
            String subject = "Sanchoi247 Refund Request Rejection";
            String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>"
                    +
                    "<h1 style='color: #dc3545; text-align: center;'>Sanchoi247</h1>" +
                    "<p style='font-size: 16px; color: #333;'>Hello <strong>" + user.getName() + "</strong>,</p>" +
                    "<p style='font-size: 16px; color: #333;'>Your refund request has been <strong style='color: #dc3545;'> rejected</strong>.</p>"
                    +
                    "<p style='font-size: 16px; color: #333;'>If you have any questions, please contact our admin at <a href='mailto:247sanchoi@gmail.com' style='color: #007bff; text-decoration: none;'>247sanchoi@gmail.com</a> for further assistance.</p>"
                    +
                    "<p style='font-size: 16px; color: #333;'>Thank you for using Sanchoi247!</p>" +
                    "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                    +
                    "<br>" +
                    "<p style='font-size: 12px; color: #555; text-align: center;'>(c) 2024 Sanchoi247. All rights reserved</p>"
                    +
                    "</div>" +
                    "</body></html>";

            // Gửi email thông báo
            emailService.sendEmail(user.getEmail(), subject, content);

            model.addAttribute("message", "Refund request rejected successfully. Email notification sent.");
        } else {
            model.addAttribute("error", "Booking is not eligible for refund rejection.");
            return "error";
        }

        return "redirect:/ViewOwnerRefundRequests";
    }

}
