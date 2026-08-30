package com.ner.logistics.user;

import com.ner.logistics.audit.AuditService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW') or hasAuthority('USER_MANAGE')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('USER_SUSPEND') or hasAuthority('USER_MANAGE')")
    public ResponseEntity<?> suspendUser(@PathVariable Long id,
                                         @RequestBody UserStatusChangeDto dto,
                                         @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for account suspension."));
        }

        return userRepository.findById(id).map(user -> {
            String oldStatus = user.getStatus().name();
            user.setStatus(UserAccountStatus.SUSPENDED);
            userRepository.save(user);

            auditService.logDetailedEvent(
                    actor != null ? actor.getUsername() : "ADMIN",
                    actor != null ? actor.getRole().name() : "ADMIN",
                    "USER_SUSPENDED",
                    "User",
                    user.getId().toString(),
                    oldStatus,
                    UserAccountStatus.SUSPENDED.name(),
                    dto.getJustificationReason(),
                    null,
                    "SUCCESS"
            );

            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reactivate")
    @PreAuthorize("hasAuthority('USER_REACTIVATE') or hasAuthority('USER_MANAGE')")
    public ResponseEntity<?> reactivateUser(@PathVariable Long id,
                                            @RequestBody UserStatusChangeDto dto,
                                            @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for account reactivation."));
        }

        return userRepository.findById(id).map(user -> {
            String oldStatus = user.getStatus().name();
            user.setStatus(UserAccountStatus.ACTIVE);
            userRepository.save(user);

            auditService.logDetailedEvent(
                    actor != null ? actor.getUsername() : "ADMIN",
                    actor != null ? actor.getRole().name() : "ADMIN",
                    "USER_REACTIVATED",
                    "User",
                    user.getId().toString(),
                    oldStatus,
                    UserAccountStatus.ACTIVE.name(),
                    dto.getJustificationReason(),
                    null,
                    "SUCCESS"
            );

            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('USER_DEACTIVATE') or hasAuthority('USER_MANAGE')")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id,
                                            @RequestBody UserStatusChangeDto dto,
                                            @AuthenticationPrincipal User actor) {
        if (dto.getJustificationReason() == null || dto.getJustificationReason().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mandatory justification reason is required for account deactivation."));
        }

        return userRepository.findById(id).map(user -> {
            String oldStatus = user.getStatus().name();
            user.setStatus(UserAccountStatus.DEACTIVATED);
            userRepository.save(user);

            auditService.logDetailedEvent(
                    actor != null ? actor.getUsername() : "ADMIN",
                    actor != null ? actor.getRole().name() : "ADMIN",
                    "USER_DEACTIVATED",
                    "User",
                    user.getId().toString(),
                    oldStatus,
                    UserAccountStatus.DEACTIVATED.name(),
                    dto.getJustificationReason(),
                    null,
                    "SUCCESS"
            );

            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Data
    public static class UserStatusChangeDto {
        private String justificationReason;
    }
}
