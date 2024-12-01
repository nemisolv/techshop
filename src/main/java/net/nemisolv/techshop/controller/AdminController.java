package net.nemisolv.techshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.payload.ApiResponse;
import net.nemisolv.techshop.payload.auth.RegisterInternalRequest;
import net.nemisolv.techshop.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AuthService authService;


    @PostMapping("/internal/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> registerInternal(@RequestBody @Valid RegisterInternalRequest authRequest) {
        authService.registerInternal(authRequest);
        return ApiResponse.success();
    }
}
