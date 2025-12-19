package com.example.springboot_01.controller;

import com.example.springboot_01.service.SmileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/smile")
@CrossOrigin
public class SmileController {

    private final SmileService smileService;

    public SmileController(SmileService smileService) {
        this.smileService = smileService;
    }

    @PostMapping(value = "/score", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> getSmileScore(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "orderId", required = false) String orderId) {

        Map<String, Object> response = new HashMap<>();
        try {
            double score = smileService.analyzeSmile(image);
            double discountRate = 1.0;
            String message = "未检测到人脸或微笑";

            if (score >= 80) {
                discountRate = 0.85;
                message = "检测成功，获得8.5折微笑折扣！";
            } else if (score >= 40) {
                discountRate = 0.9;
                message = "检测成功，获得9折微笑折扣！";
            } else if (score > 0) {
                // Detected face but low smile score
                message = "检测到人脸，但微笑程度不足，无折扣";
            } else {
                message = "未检测到人脸，请重新拍摄";
            }

            response.put("success", true);
            response.put("smileScore", Math.round(score));
            response.put("discountRate", discountRate);
            response.put("message", message);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "系统异常: " + e.getMessage());
        }
        return response;
    }
}
