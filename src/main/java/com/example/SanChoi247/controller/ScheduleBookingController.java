package com.example.SanChoi247.controller;

import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.repo.SanRepo;
import com.example.SanChoi247.model.repo.ScheduleBookingRepo;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ScheduleBookingController {
    @Autowired
    ScheduleBookingRepo scheduleBookingRepository;
    @Autowired
    SanRepo sanRepo;

    // Load the edit form with the current booking data
    @GetMapping("/editBooking/{id}")
    public String showEditBookingForm(@PathVariable("id") int bookingId, Model model) throws Exception {
        ScheduleBooking booking = scheduleBookingRepository.findById(bookingId);

        if (booking != null) {
            model.addAttribute("booking", booking); // Ensure the booking is added to the model
            return "owner/editBookingForm"; // Thymeleaf template for the edit form
        } else {
            return "redirect:/errorPage"; // Handle the case where the booking is not found
        }
    }

    @PostMapping("/editBooking")
    public String updateBooking(@ModelAttribute("booking") ScheduleBooking updatedBooking) throws Exception {
        // Get the existing booking from the database
        ScheduleBooking existingBooking = scheduleBookingRepository.findById(updatedBooking.getBooking_id());

        if (existingBooking == null) {
            throw new Exception("Booking not found");
        }

        // Update the booking with the new details from the user input
        existingBooking.setStart_time(updatedBooking.getStart_time()); // Set new start time
        existingBooking.setEnd_time(updatedBooking.getEnd_time()); // Set new end time
        existingBooking.setStatus(updatedBooking.getStatus()); // Update status
        existingBooking.setPrice(updatedBooking.getPrice()); // Update price
        existingBooking.setBooking_date(updatedBooking.getBooking_date()); // Update booking date if needed

        // Save the updated booking to the database
        scheduleBookingRepository.update(existingBooking);

        return "redirect:/"; // Redirect to the bookings list page
    }

    @PostMapping("/showAddScheduleBooking")
    public String showAddSchedule(@RequestParam("sanId") int sanId, HttpSession session, Model model) {

        session.setAttribute("sanId", sanId);

        // Trả về trang Thymeleaf để hiển thị form thêm mới lịch
        return "owner/addScheduleBooking";
    }

    @PostMapping("/addScheduleBooking")
    public String addScheduleBooking(
            @RequestParam("sanId") int sanId,
            @RequestParam("dateFrom") LocalDate dateFrom,
            @RequestParam("dateTo") LocalDate dateTo,
            @RequestParam Map<String, String> allParams) throws Exception {

        // Lọc các giá trị input người dùng đã nhập từ allParams
        List<ScheduleBooking> bookings = new ArrayList<>();

        for (String key : allParams.keySet()) {
            if (key.startsWith("startTime") || key.startsWith("endTime") || key.startsWith("status")
                    || key.startsWith("price")) {
                String value = allParams.get(key);
                if (!value.isEmpty()) {
                    String index = key.replaceAll("\\D+", ""); // Lấy số chỉ mục từ key (ví dụ: 1, 2, 3...)

                    // Lấy các giá trị từ các ô input
                    LocalTime startTime = LocalTime.parse(allParams.get("startTime" + index));
                    LocalTime endTime = LocalTime.parse(allParams.get("endTime" + index));

                    Float price = Float.parseFloat(allParams.get("price" + index));

                    // Tạo đối tượng ScheduleBooking cho từng giờ
                    ScheduleBooking booking = new ScheduleBooking();
                    San san = sanRepo.getSanById(sanId);
                    booking.setSan(san);
                    booking.setStart_time(startTime);
                    booking.setEnd_time(endTime);
                    booking.setStatus("available");
                    booking.setPrice(price);

                    bookings.add(booking);
                }
            }
        }

        // Duyệt qua từng ngày từ dateFrom đến dateTo
        LocalDate currentDate = dateFrom;
        while (!currentDate.isAfter(dateTo)) {
            for (ScheduleBooking booking : bookings) {
                // Tạo một bản sao mới của booking cho mỗi ngày
                ScheduleBooking newBooking = new ScheduleBooking();
                newBooking.setSan(booking.getSan());
                newBooking.setStart_time(booking.getStart_time());
                newBooking.setEnd_time(booking.getEnd_time());
                newBooking.setStatus(booking.getStatus());
                newBooking.setPrice(booking.getPrice());
                newBooking.setBooking_date(currentDate); // Thiết lập ngày đặt cho từng booking mới

                // Kiểm tra xem booking đã tồn tại hay chưa
                if (!scheduleBookingRepository.existsBooking(currentDate, newBooking.getStart_time(),
                        newBooking.getEnd_time(), sanId)) {
                    try {
                        scheduleBookingRepository.addScheduleBooking(newBooking); // Gọi phương thức thêm booking vào
                                                                                  // repository
                        System.out.println("Added booking: " + newBooking);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "redirect:/errorPage"; // Chuyển hướng đến trang lỗi nếu có lỗi xảy ra
                    }
                } else {
                    System.out.println("Booking already exists for: " + newBooking);
                }
            }
            currentDate = currentDate.plusDays(1); // Tiến đến ngày tiếp theo
        }

        return "redirect:/"; // Chuyển hướng đến trang thành công
    }
}
