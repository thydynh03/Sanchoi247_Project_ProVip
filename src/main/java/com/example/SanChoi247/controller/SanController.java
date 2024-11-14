package com.example.SanChoi247.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.repo.SanRepo;
import com.example.SanChoi247.model.repo.ScheduleBookingRepo;
import com.example.SanChoi247.model.repo.UserRepo;
// import com.example.SanChoi247.service.SanService;

@Controller
public class SanController {
    @Autowired
    SanRepo sanRepo;
    // @Autowired
    // SanService sanService;
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

        model.addAttribute("bookings", bookings);
        model.addAttribute("SanDetail", san);
        return "public/detailLocation";
    }

    

    // --------------------------------------------------------------------------------------//
    // @PostMapping("/incrementEyeview")
    // public ResponseEntity<?> incrementEyeview(@RequestParam int sanId) {
    // sanService.incrementEyeview(sanId);
    // int newEyeview = sanService.getEyeview(sanId);
    // return ResponseEntity.ok(newEyeview);
    // }
    @GetMapping("/getBookingsByDate")
    @ResponseBody
    public List<ScheduleBooking> getBookingsByDate(
            @RequestParam("sanId") int sanId, 
            @RequestParam("bookingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate) throws Exception {
        List<ScheduleBooking> bookings = scheduleBookingRepo.getAvailableBookingsByDate(sanId, bookingDate);
        return bookings;
    }
//     @GetMapping("/listSan")
// public String listSan(Model model) {
//     List<San> sanList = sanRepo.getAllSan();
//     for (San san : sanList) {
//         double averageRating = sanService.calculateAverageRating(san.getSan_id());
//         san.setAverageRating(averageRating);
//     }
//     model.addAttribute("userList", sanList);
//     return "index";
// }


}
