package com.example.SanChoi247.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.Map;
import com.example.SanChoi247.config.VNPayConfig;
import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.BookingRepo;
import com.example.SanChoi247.model.repo.ScheduleBookingRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookingController {
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;

    @GetMapping("/bookSan/{id}")
    public String bookSan(@PathVariable("id") int sbid, Model model, HttpSession httpSession,
            HttpServletRequest httpRequest) throws Exception {
        ScheduleBooking scheduleBooking = scheduleBookingRepo.findById(sbid);
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        if (scheduleBooking == null) {
            model.addAttribute("error", "Schedule booking not found.");
            return "error";
        }
        // Create a new booking
        Booking booking = new Booking();
        booking.setDate(LocalDateTime.now());
        booking.setUser(user);
        booking.setSan(scheduleBooking.getSan());
        booking.setScheduleBooking(scheduleBooking);
        booking.setTotalprice(scheduleBooking.getPrice());
        booking.setStatus(Booking.PaymentStatus.PENDING);

        // Save booking with pending status
        int id = bookingRepo.pushBooking(booking);
        httpSession.setAttribute("booking_id", id);
        // Generate VNPay payment URL
        String vnpayUrl = VNPayConfig.generateVnpayUrl(String.valueOf(id),
                String.valueOf((long) (scheduleBooking.getPrice())));
        // Redirect the user to VNPay for payment
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
        if (vnpResponseCode.equals("00")) { // Thanh toán thành công
            booking.setStatus(Booking.PaymentStatus.SUCCESS);
            model.addAttribute("message", "Đặt sân thành công!");
            model.addAttribute("error", "Chúc mừng! Bạn đã thanh toán thành công.");
            ScheduleBooking scheduleBooking = scheduleBookingRepo
                    .findById(booking.getScheduleBooking().getBooking_id());
            scheduleBookingRepo.updateScheduleBookingStatus(scheduleBooking.getBooking_id(), "Booked");
        } else { // Thanh toán thất bại
            booking.setStatus(Booking.PaymentStatus.FAILED);
            model.addAttribute("message", "Đặt sân thất bại!");
            model.addAttribute("error", "Rất tiếc, đã xảy ra lỗi trong quá trình thanh toán. Vui lòng thử lại sau.");
        }
        // Update booking status
        bookingRepo.save(booking); // Bỏ dấu // để lưu lại booking
        // Redirect or return a view to notify the user about the payment status
        return "public/result"; // Kiểm tra rằng template này tồn tại
    }

    public String bookSan(@PathVariable("id") int sbid, Model model, HttpSession httpSession,
            HttpServletRequest httpRequest) throws Exception {
        ScheduleBooking scheduleBooking = scheduleBookingRepo.findById(sbid);
        User user = (User) httpSession.getAttribute("UserAfterLogin");

        if (scheduleBooking == null) {
            model.addAttribute("error", "Schedule booking not found.");
            return "error";
        }

        // Create a new booking
        Booking booking = new Booking();
        booking.setDate(LocalDateTime.now());
        booking.setUser(user);
        booking.setSan(scheduleBooking.getSan());
        booking.setScheduleBooking(scheduleBooking);
        booking.setTotalprice(scheduleBooking.getPrice());
        booking.setStatus(Booking.PaymentStatus.PENDING);

        // Save booking with pending status
        int id = bookingRepo.pushBooking(booking);
        httpSession.setAttribute("booking_id", id);
        // Generate VNPay payment URL

        String vnpayUrl = VNPayConfig.generateVnpayUrl(String.valueOf(id),
                String.valueOf((long) (scheduleBooking.getPrice())));

        // Redirect the user to VNPay for payment
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

        if (vnpResponseCode.equals("00")) { // Thanh toán thành công
            booking.setStatus(Booking.PaymentStatus.SUCCESS);
            model.addAttribute("message", "Đặt sân thành công!");
            model.addAttribute("error", "Chúc mừng! Bạn đã thanh toán thành công.");
            ScheduleBooking scheduleBooking = scheduleBookingRepo
                    .findById(booking.getScheduleBooking().getBooking_id());
            scheduleBookingRepo.updateScheduleBookingStatus(scheduleBooking.getBooking_id(), "Booked");
        } else { // Thanh toán thất bại
            booking.setStatus(Booking.PaymentStatus.FAILED);
            model.addAttribute("message", "Đặt sân thất bại!");
            model.addAttribute("error", "Rất tiếc, đã xảy ra lỗi trong quá trình thanh toán. Vui lòng thử lại sau.");
        }

        // Update booking status
        bookingRepo.save(booking); // Bỏ dấu // để lưu lại booking

        // Redirect or return a view to notify the user about the payment status
        return "public/result"; // Kiểm tra rằng template này tồn tại
    }

}