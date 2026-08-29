package com.ner.logistics.config;

import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRepository;
import com.ner.logistics.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@sih.gov.in")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(UserRole.ADMIN)
                    .fullName("SIH System Administrator")
                    .phoneNumber("+91 9876543210")
                    .build());

            userRepository.save(User.builder()
                    .username("operator")
                    .email("operator@sih.gov.in")
                    .password(passwordEncoder.encode("Operator@123"))
                    .role(UserRole.LOGISTICS_OPERATOR)
                    .fullName("Logistics Command Officer")
                    .phoneNumber("+91 9876543211")
                    .build());

            userRepository.save(User.builder()
                    .username("officer")
                    .email("officer@sih.gov.in")
                    .password(passwordEncoder.encode("Officer@123"))
                    .role(UserRole.FIELD_OFFICER)
                    .fullName("Haflong Field Officer")
                    .phoneNumber("+91 9876543212")
                    .build());

            userRepository.save(User.builder()
                    .username("driver")
                    .email("driver@sih.gov.in")
                    .password(passwordEncoder.encode("Driver@123"))
                    .role(UserRole.DRIVER)
                    .fullName("Convoy Driver NER-07")
                    .phoneNumber("+91 9876543213")
                    .build());
        }
    }
}
