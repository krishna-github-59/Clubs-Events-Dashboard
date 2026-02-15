package com.club.events_dashboard.entity;

import com.club.events_dashboard.constants.Role;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@iiitm\\.ac\\.in$", message = "email must be from iiitm.ac.in domain")
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = true)
    private Club club;

    public User() {}

    public User(String name, String email, String password, Role role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    } 

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }

    public Club getClub(){
        return club;
    }
    
    public void setClub(Club club){
        this.club = club;
    }
}
