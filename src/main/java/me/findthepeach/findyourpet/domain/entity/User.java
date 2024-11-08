package me.findthepeach.findyourpet.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "user_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String firebaseUid;

    private String name;

    private String email;

    private String avatarUrl;

}