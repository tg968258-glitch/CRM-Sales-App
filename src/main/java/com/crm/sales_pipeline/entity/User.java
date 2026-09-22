package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.UserRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @Column(name = "uid", length = 10)
    private String uid;

    @Column(name = "name", nullable=false)
    private String name;

    @Column(name = "email", length = 255, nullable=false,unique = true)
    private String email;

    @Column(name="password_hash", nullable=false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at",nullable=false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
