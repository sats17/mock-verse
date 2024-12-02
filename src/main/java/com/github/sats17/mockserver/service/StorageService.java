package com.github.sats17.mockserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.mockserver.controller.MockController;
import com.github.sats17.mockserver.model.Input;
import com.github.sats17.mockserver.model.InputDTO;
import com.github.sats17.mockserver.model.h2.Storage;
import com.github.sats17.mockserver.repository.StorageRepository;
import com.github.sats17.mockserver.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    ObjectMapper objectMapper;


    public Boolean saveStorageToDB(InputDTO inputDTO) throws JsonProcessingException {

        String structuredQueryParams = Utility.generateQueryParamString(inputDTO.getApiQueryParameters());
        HashMap<String, String> headers = Utility.generateAPIHeaders(inputDTO.getApiHeaders());
        MediaType contentType = Utility.resolveContentType(headers.get("content-type"));

        Input body = inputDTO.getBody();
        String mockKey;
        String normalizedRequestBody = null;
        if (body.getRequest() != null) {
            logger.debug("Received mock behaviour with request body, mock verse will try to hash body request");
            normalizedRequestBody = objectMapper.writeValueAsString(body.getRequest());
            logger.debug("Normalized Request JSON Body: {}", normalizedRequestBody);
            String hashedRequestBody;
            try {
                hashedRequestBody = Utility.generateHashCodeFromString(normalizedRequestBody);
            } catch (NoSuchAlgorithmException e) {
                logger.debug("Error occurred while generating hash from request body: {}", e.getMessage());
                return false;
            }
            mockKey = Utility.generateMockStorageKey(inputDTO.getApiMethod(), inputDTO.getApiPath(), structuredQueryParams, hashedRequestBody);
        } else {
            mockKey = Utility.generateMockStorageKey(inputDTO.getApiMethod(), inputDTO.getApiPath(), structuredQueryParams);
        }

        String normalizedResponseBody = objectMapper.writeValueAsString(body.getResponse());

        Storage storage = new Storage(mockKey, structuredQueryParams, contentType.toString(), inputDTO.getApiPath(),
                normalizedRequestBody, normalizedResponseBody);
        Storage resp = storageRepository.save(storage);
        return true;
    }

    public Storage getStorageByKey(String key) {
        Optional<Storage> storage = storageRepository.findById(key);
        if(storage.isPresent()) {
            return storage.get();
        } else {
            throw new RuntimeException("Data not found");
        }
    }


}
