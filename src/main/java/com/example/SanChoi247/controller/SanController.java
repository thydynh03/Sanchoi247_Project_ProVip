package com.example.SanChoi247.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.SanRepo;
import com.example.SanChoi247.model.repo.ScheduleBookingRepo;
import com.example.SanChoi247.model.repo.UserRepo;

@Controller
public class SanController {
    @Autowired
    SanRepo sanRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;
    @Autowired
    UserRepo userRepo;

    @GetMapping("/ViewDetail/{id}")
    public String viewDetail(@PathVariable("id") int uid, Model model) throws Exception {
        ArrayList<San> sanList = new ArrayList<>();
        sanList = sanRepo.getAllSanByChuSanId(uid);
        model.addAttribute("SanDetail", sanList);
        return "public/viewDeTail";
    }

    @GetMapping("/ShowDetailLocation/{id}")
    public String showDetailLocation(@PathVariable("id") int sid, Model model) throws Exception {
        San san = sanRepo.getSanById(sid);
        List<ScheduleBooking> bookings = scheduleBookingRepo.getAvailableBookings(sid);

        // Thêm dữ liệu vào model để hiển thị trên view
        model.addAttribute("bookings", bookings);
        model.addAttribute("SanDetail", san);
        return "public/detailLocation";
    }

    // --------------------------------------------------------------------------------------//

    // @PostMapping("/SearchSanByTenSan")
    // public String searchSanByTenSan(@RequestParam("Search") String Search, Model
    // model) throws Exception {
    // ArrayList<User> userList = userRepo.getAllUser();
    // ArrayList<User> findSan = new ArrayList<>();
    // for (User tenSan : userList) {
    // if (tenSan != null &&
    // tenSan.getTen_san().toLowerCase().contains((Search.toLowerCase()))) {
    // findSan.add(tenSan);
    // }
    // }
    // model.addAttribute("SanList", findSan);
    // return "public/index";
    // }

}
