package com.ner.logistics.auth;

import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRepository;
import com.ner.logistics.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    // Concurrent in-memory store for OTPs (Key: phoneNumber, Value: OTP code)
    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    public String sendOtp(OtpSendRequestDto dto) {
        String phone = dto.getPhoneNumber().trim();
        // Demo/Static OTP 123456 or random 6-digit number
        String otp = "123456"; 

        otpCache.put(phone, otp);
        log.info("📲 OTP generated for mobile number {}: {}", phone, otp);

        return "OTP sent successfully to " + phone + ". (Demo OTP: 123456)";
    }

    @Transactional
    public AuthResponse verifyOtpAndLogin(OtpVerifyRequestDto dto) {
        String phone = dto.getPhoneNumber().trim();
        String enteredOtp = dto.getOtp().trim();

        String cachedOtp = otpCache.get(phone);
        if (cachedOtp == null || !cachedOtp.equals(enteredOtp)) {
            throw new IllegalArgumentException("Invalid or expired OTP code.");
        }

        // OTP verified successfully - clear from cache
        otpCache.remove(phone);

        // Find existing user by phone number or auto-register new Driver profile
        Optional<User> userOpt = userRepository.findByPhoneNumber(phone);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // Auto-register non-technical driver user
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            String username = "driver_" + cleanPhone;
            
            user = User.builder()
                    .username(username)
                    .email(username + "@sih.gov.in")
                    .password(passwordEncoder.encode("DriverOtp@2026"))
                    .role(UserRole.DRIVER)
                    .fullName("Convoy Driver (" + phone + ")")
                    .phoneNumber(phone)
                    .build();
            user = userRepository.save(user);
            log.info("👤 Auto-registered new Driver profile for phone number {}", phone);
        }

        String jwtToken = tokenProvider.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .build();
    }
}
