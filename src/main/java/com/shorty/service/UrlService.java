package com.shorty.service;

import com.shorty.model.Url;
import com.shorty.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {

    public Url generateShortUrl(UrlDto urlDto);
    public Url getEncodedUrl(String url);
    public void deleteUrl(String url);
}
