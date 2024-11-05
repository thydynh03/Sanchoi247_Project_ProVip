package com.example.SanChoi247.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.SanRepo;
import com.example.SanChoi247.model.repo.ScheduleBookingRepo;
import com.example.SanChoi247.model.repo.UserRepo;

@Controller
public class SearchController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    SanRepo sanRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;

    public static String removeAccent(String text) {
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    // phuong thuc search
    @PostMapping("/SearchSanByTenSan")
    public String searchSanByTenSan(@RequestParam("Search") String Search,
            @RequestParam("Location") String Location,
            @RequestParam("SportType") String SportType,
            Model model) throws Exception {
        ArrayList<User> userList = userRepo.getAllUser();
        ArrayList<User> findSan = new ArrayList<>();

        // Xử lý từ khóa tìm kiếm
        String searchNormalized = removeAccent(Search).toLowerCase();
        String locationNormalized = removeAccent(Location).toLowerCase();
        String sportTypeNormalized = removeAccent(SportType).toLowerCase();
        if (sportTypeNormalized.equals("type")) {
            sportTypeNormalized = "";
        }
        if (locationNormalized.equals("location")) {
            locationNormalized = "";
        }
        String nullValue = "";
        for (User tenSan : userList) {
            boolean matchesSearch = false;
            boolean matchesLocation = false;
            boolean matchesSportType = false;

            // Kiểm tra điều kiện theo tên sân
            if (tenSan.getTen_san() != null && tenSan.getRole() == 'C' && !searchNormalized.equals(nullValue)) {
                String tenSanNormalized = removeAccent(tenSan.getTen_san()).toLowerCase();
                if (tenSanNormalized.contains(searchNormalized)) {
                    matchesSearch = true;
                }
            }

            // Kiểm tra điều kiện theo quận (Location)
            if (tenSan.getAddress() != null && tenSan.getRole() == 'C' && !locationNormalized.equals(nullValue)) {
                String addressNormalized = removeAccent(tenSan.getAddress()).toLowerCase();
                if (addressNormalized.contains(locationNormalized)) {
                    matchesLocation = true;
                }
            }

            // Kiểm tra điều kiện theo loại sân (SportType)
            San sanDetails = sanRepo.getSanById(tenSan.getUid());
            ArrayList<San> sanlist = sanRepo.getAllSanByChuSanId(tenSan.getUid());
            System.out.println(sanDetails.getLoaiSan().getLoai_san_type());
            if (!sanDetails.getLoaiSan().getLoai_san_type().equals("") && tenSan.getRole() == 'C'
                    && !sportTypeNormalized.equals(nullValue)) {
                for (San Sans : sanlist) {
                    String loaiSanNormalized = removeAccent(Sans.getLoaiSan().getLoai_san_type()).toLowerCase();
                    if (loaiSanNormalized.contains(sportTypeNormalized)) {
                        matchesSportType = true;
                        break;
                    }
                }

            }
            if (matchesSearch || matchesLocation || matchesSportType) {
                findSan.add(tenSan);
            }
        }

        if (findSan.isEmpty()) {
            model.addAttribute("message", "not found");
        } else {
            model.addAttribute("userList", findSan);
        }

        return "search/searchResult";
    }

    // --------------------------------------------------------------------------------------------------------//
    // Show trang search

    @GetMapping("/ShowSearch")
    public String showSearch() {
        return "search/searchResult";
    }

    // --------------------------------------------------------------------------------------------------------//

    // @PostMapping("/SearchSanTheoKhungGio")
    // public String searchSanTheoKhungGio(@RequestParam("SearchDate") String
    // searchDate,
    // @RequestParam("StartTime") String startTime,
    // @RequestParam("EndTime") String endTime,
    // Model model) throws Exception {
    // // Chuyển đổi các giá trị từ form thành các kiểu dữ liệu thời gian
    // LocalDate date = LocalDate.parse(searchDate);
    // LocalTime start = LocalTime.parse(startTime);
    // LocalTime end = LocalTime.parse(endTime);

    // // Danh sách các sân tìm thấy
    // ArrayList<San> foundSanh = new ArrayList<>();

    // // Lấy danh sách tất cả các sân từ cơ sở dữ liệu
    // ArrayList<San> allSan = sanRepo.getAllSan();

    // // Lặp qua từng sân để kiểm tra thời gian đặt
    // for (San san : allSan) {
    // // Lấy lịch đặt sân cho sân này vào ngày cụ thể
    // List<ScheduleBooking> bookings =
    // scheduleBookingRepo.findBySanAndDate(san.getSan_id(), date);

    // boolean available = true;
    // for (ScheduleBooking booking : bookings) {
    // // Chỉ kiểm tra những booking có trạng thái là "available"
    // if (booking.getStatus().equalsIgnoreCase("available")) {
    // // Kiểm tra nếu có khung giờ nào đã đặt trong khoảng thời gian tìm kiếm
    // if (!(end.isBefore(booking.getStart_time()) ||
    // start.isAfter(booking.getEnd_time()))) {
    // available = false;
    // break; // Nếu không có thời gian trống thì ngừng kiểm tra
    // }
    // } else {
    // available = false;
    // break;
    // }
    // }

    // // Nếu sân này có sẵn khung giờ, thêm vào danh sách kết quả
    // if (available) {
    // foundSanh.add(san);
    // }
    // }

    // // Nếu không tìm thấy sân phù hợp, trả về thông báo
    // if (foundSanh.isEmpty()) {
    // model.addAttribute("message", "No available stadiums found for the selected
    // time.");
    // } else {
    // model.addAttribute("foundSanh", foundSanh);
    // }

    // // Trả về trang kết quả tìm kiếm
    // return "public/viewDetail";
    // }

    @PostMapping("/SearchSanTheoKhungGio")
    public String searchSanTheoKhungGio(@RequestParam("SearchDate") String searchDate,
            @RequestParam("StartTime") String startTime,
            @RequestParam("EndTime") String endTime,
            Model model) throws Exception {
        // Chuyển đổi chuỗi thành các đối tượng ngày giờ
        LocalDate date = LocalDate.parse(searchDate);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // Danh sách các sân và các sân tìm thấy
        ArrayList<San> sanList = sanRepo.getAllSan();
        ArrayList<San> findSan = new ArrayList<>();

        // Lặp qua từng sân trong danh sách
        for (San san : sanList) {
            // Lấy danh sách ScheduleBooking cho sân này vào ngày cụ thể
            List<ScheduleBooking> bookings = scheduleBookingRepo.findBySanAndDate(san.getSan_id(), date);

            boolean available = false;

            // Lặp qua các booking để kiểm tra xem có phù hợp khung giờ và status là
            // "available"
            for (ScheduleBooking booking : bookings) {
                // Kiểm tra nếu khung giờ tìm kiếm nằm hoàn toàn bên ngoài khoảng thời gian của
                // booking đã đặt
                if (booking.getStatus().equals("available") &&
                        (start.isAfter(booking.getEnd_time()) || end.isBefore(booking.getStart_time()))) {
                    available = true;
                } else {
                    available = false;
                    break; // Nếu không có thời gian trống thì ngừng kiểm tra
                }
            }

            // Nếu tìm thấy sân có sẵn trong khung giờ, thêm vào danh sách kết quả
            if (available) {
                findSan.add(san);
            }
        }

        // Nếu không tìm thấy sân phù hợp, trả về thông báo
        if (findSan.isEmpty()) {
            model.addAttribute("message", "No available stadiums found for the selected time.");
        } else {
            model.addAttribute("SanList", findSan);
        }

        // Trả về trang kết quả tìm kiếm
        return "public/viewDetail";
    }

}
