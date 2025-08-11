package com.ecovery.service;

import com.ecovery.dto.AIPredictionResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface  AIService {

    AIPredictionResultDto predictDisposal(MultipartFile imageFile, String regionGu) throws IOException, Exception;
}