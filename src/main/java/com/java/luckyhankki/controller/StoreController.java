package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.StoreRequest;
import com.java.luckyhankki.dto.StoreResponse;
import com.java.luckyhankki.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<StoreResponse> registerStore(
            @RequestParam Long sellerId,
            @Valid @RequestBody StoreRequest store
    ) {
        StoreResponse storeResponse = storeService.registerStore(sellerId, store);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeResponse);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long sellerId) {
        StoreResponse storeResponse = storeService.findStore(sellerId);

        return ResponseEntity.status(HttpStatus.OK).body(storeResponse);
    }
}
