package com.example.SanChoi247.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import com.example.SanChoi247.config.VNPayConfig;

import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.Booking.PaymentStatus;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.BookingRepo;
import com.example.SanChoi247.model.repo.ScheduleBookingRepo;
import com.example.SanChoi247.model.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookingController {
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;
    @Autowired
    EmailService emailService;

    @PostMapping("/bookSan")
    public String bookSan(@RequestParam("bookingId") int sbid, Model model, HttpSession httpSession,
            HttpServletRequest httpRequest) throws Exception {
        // Lấy lịch đặt sân từ repo
        ScheduleBooking scheduleBooking = scheduleBookingRepo.findById(sbid);
        User user = (User) httpSession.getAttribute("UserAfterLogin");

        if (scheduleBooking == null) {
            model.addAttribute("error", "Schedule booking not found.");
            return "error";
        }

        // Tạo một booking mới
        Booking booking = new Booking();
        booking.setDate(LocalDateTime.now());
        booking.setUser(user);
        booking.setSan(scheduleBooking.getSan());
        booking.setScheduleBooking(scheduleBooking);
        booking.setTotalprice(scheduleBooking.getPrice());
        booking.setStatus(Booking.PaymentStatus.PENDING);

        // Chưa có dữ liệu vnpayData, để trống ban đầu
        booking.setVnpayData(null); // Hoặc "" nếu cần chuỗi trống

        // Lưu booking với trạng thái PENDING
        int id = bookingRepo.pushBooking(booking);
        httpSession.setAttribute("booking_id", id);

        // Tạo URL VNPay để thanh toán
        String vnpayUrl = VNPayConfig.generateVnpayUrl(String.valueOf(id),
                String.valueOf((long) (scheduleBooking.getPrice())));

        // Cập nhật trạng thái scheduleBooking thành "pending"
        scheduleBookingRepo.updateScheduleBookingStatus(scheduleBooking.getBooking_id(), "pending");

        // Chuyển hướng người dùng tới trang thanh toán VNPay
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay_return")
    public String vnpayReturn(@RequestParam Map<String, String> requestParams, Model model, HttpSession httpSession)
            throws NumberFormatException, Exception {
        // Extract payment result from requestParams
        String vnpResponseCode = requestParams.get("vnp_ResponseCode");
        String txnRef = requestParams.get("vnp_TxnRef");

        // Kiểm tra txnRef
        if (txnRef == null || txnRef.isEmpty()) {
            model.addAttribute("error", "Transaction reference not found.");
            return "public/result"; // Trả về trang lỗi
        }

        // Find booking by transaction reference
        int id = (int) httpSession.getAttribute("booking_id");
        Booking booking = bookingRepo.findById(id);

        if (booking == null) {
            model.addAttribute("error", "Booking not found for transaction reference: " + txnRef);
            return "public/result"; // Trả về trang lỗi
        }

        // Chuyển đổi requestParams thành chuỗi JSON hợp lệ
        ObjectMapper objectMapper = new ObjectMapper();
        String vnpayDataJson = objectMapper.writeValueAsString(requestParams); // Chuyển map thành chuỗi JSON hợp lệ
        booking.setVnpayData(vnpayDataJson); // Lưu chuỗi JSON vào vnpayData

        if ("00".equals(vnpResponseCode)) { // Thanh toán thành công
            booking.setStatus(Booking.PaymentStatus.SUCCESS);
            model.addAttribute("message", "Đặt sân thành công!");
            model.addAttribute("error", "Chúc mừng! Bạn đã thanh toán thành công.");
            model.addAttribute("success", true); // Thêm thuộc tính thành công

            // Cập nhật trạng thái lịch đặt sân thành "Booked"
            ScheduleBooking scheduleBooking = scheduleBookingRepo
                    .findById(booking.getScheduleBooking().getBooking_id());
            scheduleBookingRepo.updateScheduleBookingStatus(scheduleBooking.getBooking_id(), "Booked");
        } else { // Thanh toán thất bại
            booking.setStatus(Booking.PaymentStatus.FAILED);
            model.addAttribute("message", "Đặt sân thất bại!");
            model.addAttribute("error", "Rất tiếc, đã xảy ra lỗi trong quá trình thanh toán. Vui lòng thử lại sau.");
            model.addAttribute("success", false); // Thêm thuộc tính thất bại

            // Cập nhật trạng thái lịch đặt sân thành "available"
            ScheduleBooking scheduleBooking = scheduleBookingRepo
                    .findById(booking.getScheduleBooking().getBooking_id());
            scheduleBookingRepo.updateScheduleBookingStatus(scheduleBooking.getBooking_id(), "available");
        }

        // Lưu trạng thái mới của booking
        bookingRepo.save(booking);

        // Thêm thông tin booking vào model
        model.addAttribute("booking", booking);

        // Redirect hoặc trả về view thông báo cho người dùng về trạng thái thanh toán
        return "public/result"; // Đảm bảo rằng template này tồn tại
    }

    @GetMapping("/ShowBookingByUserId")
    public String showOrderByUserId(HttpSession httpSession, Model model) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");

        // Kiểm tra nếu user chưa đăng nhập
        if (user == null) {
            return "redirect:/login"; // Điều hướng về trang đăng nhập nếu chưa đăng nhập
        }

        // Lấy danh sách booking của user
        ArrayList<Booking> bookingList = bookingRepo.getAllBookingByUserId(user.getUid());

        if (bookingList == null || bookingList.isEmpty()) {
            model.addAttribute("error", "Không có đặt sân nào được tìm thấy.");
        } else {
            model.addAttribute("BookingList", bookingList); // Truyền danh sách booking vào model
        }

        return "user/viewBooking"; // Đảm bảo rằng template này tồn tại
    }

    @PostMapping("/requestRefund")
    public String requestRefund(@RequestParam("booking_id") int bookingId, HttpSession session, Model model)
            throws Exception {
        Booking booking = bookingRepo.findById(bookingId);

        // Check if booking is null or the status is not SUCCESS
        if (booking == null || booking.getStatus() != PaymentStatus.SUCCESS) {
            model.addAttribute("error", "Invalid booking or booking is not eligible for refund.");
            return "error";
        }

        // Fetch the associated ScheduleBooking to get the start_time
        ScheduleBooking scheduleBooking = scheduleBookingRepo.findById(booking.getScheduleBooking().getBooking_id());
        if (scheduleBooking == null) {
            model.addAttribute("error", "No schedule associated with this booking.");
            return "error";
        }

        // Calculate the time difference between the current time and start_time
        LocalDateTime startTime = LocalDateTime.of(scheduleBooking.getBooking_date(), scheduleBooking.getStart_time());
        LocalDateTime currentTime = LocalDateTime.now();
        long minutesDifference = Duration.between(currentTime, startTime).toMinutes();

        // Check if a refund has already been requested
        if (booking.getStatus() != null && booking.getStatus() != PaymentStatus.SUCCESS) {
            model.addAttribute("error", "Refund request already submitted.");
            return "error";
        }

        // Determine the refund type based on the time difference
        if (minutesDifference > 120) {
            // More than 2 hours before start_time
            booking.setStatus(PaymentStatus.PENDING_TOTAL_REFUND);
        } else if (minutesDifference > 30) {
            // Between 2 hours and 30 minutes before start_time
            booking.setStatus(PaymentStatus.PENDING_PARTIAL_REFUND);
        }

        // Save the booking with the updated status
        bookingRepo.save(booking);

        // Add success message and redirect
        model.addAttribute("message", "Refund request submitted. Awaiting admin approval.");
        return "redirect:/ShowBookingByUserId";
    }

}