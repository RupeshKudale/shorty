package com.shorty.controller;

import com.shorty.model.UrlDto;
import com.shorty.model.Url;
import com.shorty.model.UrlErrorResponse;
import com.shorty.model.UrlResponseDto;
import com.shorty.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlController {

    @Autowired
    UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody UrlDto urlDto) {
        Url urlObj = urlService.generateShortUrl(urlDto);

        if(urlObj != null) {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setShortUrl(urlObj.getShortUrl());
            urlResponseDto.setOriginalUrl(urlObj.getOriginalUrl());
            urlResponseDto.setExpiryDate(urlObj.getExpiresDate());

            return ResponseEntity.ok(urlResponseDto);
        }

        UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
        urlErrorResponse.setStatus("500");
        urlErrorResponse.setMessage("Error while processing request, Please try again.");
        return ResponseEntity.ok(urlErrorResponse);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> getUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        if(shortUrl.isEmpty()) {
            UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
            urlErrorResponse.setStatus("404");
            urlErrorResponse.setMessage("Bad Request.");
            return ResponseEntity.ok(urlErrorResponse);
        }
        Url urlObj = urlService.getEncodedUrl(shortUrl);
        if(urlObj == null) {
            UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
            urlErrorResponse.setStatus("404");
            urlErrorResponse.setMessage("Url Not Found.");
            return ResponseEntity.ok(urlErrorResponse);
        }

        if(urlObj.getExpiresDate().isBefore(LocalDateTime.now())) {
            urlService.deleteUrl(urlObj.getShortUrl());
            UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
            urlErrorResponse.setStatus("404");
            urlErrorResponse.setMessage("Desired URl is expired, Please create a new one.");
            return ResponseEntity.ok(urlErrorResponse);
        }

        response.sendRedirect(urlObj.getOriginalUrl());
        return null;
    }
}
