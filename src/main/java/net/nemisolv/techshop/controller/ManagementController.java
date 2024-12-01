package net.nemisolv.techshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.payload.ApiResponse;
import net.nemisolv.techshop.payload.auth.RegisterInternalRequest;
import net.nemisolv.techshop.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manage")
public class ManagementController {

    private final AuthService authService;


    @PostMapping("/internal/register-employee")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> registerInternal(@RequestBody @Valid RegisterInternalRequest authRequest) {
        authService.registerInternal(authRequest);
        return ApiResponse.success();
    }
}
