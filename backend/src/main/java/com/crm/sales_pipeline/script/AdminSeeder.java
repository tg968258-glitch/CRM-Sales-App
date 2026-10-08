package com.crm.sales_pipeline.script;

import com.crm.sales_pipeline.entity.Role;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.enums.UserRole;
import com.crm.sales_pipeline.repository.RoleRepository;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;
    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository,
                       RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail) == null) {
            Role adminRole = roleRepository.findById(1L)
                    .orElse(null);

            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setRoleName(UserRole.ADMIN);
                adminRole.setDescription("Administrator");

                adminRole = roleRepository.save(adminRole);
            }
            User admin = new User();

            admin.setUid(1L);
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setRole(adminRole);

            userRepository.save(admin);

            System.out.println("Admin created successfully");
        }
    }}