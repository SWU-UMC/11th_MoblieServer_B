package com.umc.study.controller;

import com.umc.study.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rentals") // 대여 관련 요청의 기본 주소는 /rentals
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    // POST /rentals : Body로 userId, bookId를 받아 대여 기록을 생성합니다.
    @PostMapping
    public String createRental(@RequestBody Map<String, Object> body) {
        rentalService.createRental(body);
        return "대여 기록이 생성되었습니다!";
    }
}
