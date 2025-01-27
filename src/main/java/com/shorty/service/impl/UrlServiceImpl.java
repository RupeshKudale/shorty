package com.shorty.service.impl;

import com.google.common.hash.Hashing;
import com.shorty.model.Url;
import com.shorty.model.UrlDto;
import com.shorty.repository.UrlRepository;
import com.shorty.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
@Component
public class UrlServiceImpl implements UrlService {

    @Autowired
    UrlRepository urlRepository;

    @Override
    public Url generateShortUrl(UrlDto urlDto) {
        if(!urlDto.getUrl().isEmpty()) {
            String encodedUrl = encodeUrl(urlDto.getUrl());
            Url shortUrl = new Url();
            shortUrl.setOriginalUrl(urlDto.getUrl());
            shortUrl.setShortUrl(encodedUrl);
            shortUrl.setCreatedDate(LocalDateTime.now());
            shortUrl.setExpiresDate(setUrlExpiry(urlDto.getExpiryDate(), shortUrl.getCreatedDate()));

            return urlRepository.save(shortUrl);
        }
        return null;
    }

    private LocalDateTime setUrlExpiry(String expiryDate, LocalDateTime createdDate) {
        if(expiryDate.isEmpty()) {
            return createdDate.plusSeconds(30);
        }

        LocalDateTime newExpiryDate;
        newExpiryDate = LocalDateTime.parse(expiryDate);
        return newExpiryDate;
    }

    private String encodeUrl(String url) {
        LocalDateTime time = LocalDateTime.now();
        String encodedUrl = "";
        encodedUrl = Hashing.murmur3_32_fixed()
                .hashString(url.concat(time.toString()), StandardCharsets.UTF_8)
                .toString();
        return encodedUrl;
    }

    @Override
    public Url getEncodedUrl(String url) {
        return urlRepository.findByShortUrl(url);
    }

    @Override
    public void deleteUrl(String url) {
        Url urlToDelete = urlRepository.findByShortUrl(url);
        urlRepository.delete(urlToDelete);
    }
}
