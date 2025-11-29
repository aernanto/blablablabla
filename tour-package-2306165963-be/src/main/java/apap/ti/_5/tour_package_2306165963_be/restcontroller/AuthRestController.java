package apap.ti._5.tour_package_2306165963_be.restcontroller;

import apap.ti._5.tour_package_2306165963_be.dto.auth.*;
import apap.ti._5.tour_package_2306165963_be.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        try {
            AuthResponseDto response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", HttpStatus.CREATED.value(),
                            "message", "Registration successful",
                            "timestamp", new Date(),
                            "data", response
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", e.getMessage(),
                            "timestamp", new Date()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "Registration failed: " + e.getMessage(),
                            "timestamp", new Date()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        try {
            AuthResponseDto response = authenticationService.login(request);
            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "Login successful",
                    "timestamp", new Date(),
                    "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "message", "Invalid username or password",
                            "timestamp", new Date()
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "message", "Missing or invalid Authorization header",
                                "timestamp", new Date()
                        ));
            }

            String token = authHeader.substring(7);
            UserDto user = authenticationService.getCurrentUser(token);

            return ResponseEntity.ok(Map.of(
                    "status", HttpStatus.OK.value(),
                    "message", "User retrieved successfully",
                    "timestamp", new Date(),
                    "data", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "message", "Invalid or expired token",
                            "timestamp", new Date()
                    ));
        }
    }
}