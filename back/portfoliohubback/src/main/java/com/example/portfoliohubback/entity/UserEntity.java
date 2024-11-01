package com.example.portfoliohubback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntity implements UserDetails {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "password",length = 100)
    private String password;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "name",  length = 50)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "img_url")
    private String img_url;
    @Column(name = "is_delete")
    private boolean isDelete;
    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.REMOVE},
            fetch = FetchType.EAGER
    )
    private List<EventEntity> events;


    // 생성될때, 자동으로 현재 시간을 넣어줌
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 시간을 자동으로 넣어줌
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public UserEntity(String userId) {
        this.id = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }
    // 사용자의 id 반환(고유한 값)
    @Override
    public String getUsername() {
        return id;
    }

    // 사용자의 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // true : 만료 X
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; // true : 잠금 X
    }

    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true : 만료 X
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true; // true : 사용 가능
    }
}
