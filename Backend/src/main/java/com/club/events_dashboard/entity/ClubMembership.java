// package com.club.events_dashboard.entity;

// import jakarta.persistence.*;
// import java.time.LocalDate;

// @Entity
// @Table(name = "club_memberships",uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "club_id"}))
// public class ClubMembership {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     // Many memberships can belong to one user
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id", nullable = false)
//     private User user;

//     // Many memberships can belong to one club
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "club_id", nullable = false)
//     private Club club;

//     // user joined date in club
//     private LocalDate joinedDate = LocalDate.now();


//     public ClubMembership() {}

//     public ClubMembership(User user, Club club) {
//         this.user = user;
//         this.club = club;
//     }

//     // Getters & setters
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public User getUser() { return user; }
//     public void setUser(User user) { this.user = user; }

//     public Club getClub() { return club; }
//     public void setClub(Club club) { this.club = club; }

//     public LocalDate getJoinedDate() { return joinedDate; }
//     public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
// }
