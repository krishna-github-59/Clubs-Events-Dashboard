// package com.club.events_dashboard.service;

// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.club.events_dashboard.dto.ApiResponse;
// import com.club.events_dashboard.dto.GuestLoginDTO;
// import com.club.events_dashboard.dto.GuestRegisterDTO;
// import com.club.events_dashboard.dto.JwtResponseDTO;
// import com.club.events_dashboard.entity.Guest;
// import com.club.events_dashboard.entity.Role;
// import com.club.events_dashboard.repository.GuestRepository;
// import com.club.events_dashboard.security.JwtUtil;

// @Service
// public class GuestAuthService {
//     @Autowired
//     private GuestRepository guestRepository;

//     @Autowired
//     private BCryptPasswordEncoder passwordEncoder;

//     @Autowired
//     private JwtUtil jwtUtil;

//     public ResponseEntity<ApiResponse> guestRegister(GuestRegisterDTO dto){
//         if(guestRepository.findByEmail(dto.getEmail()).isPresent()){
//             return ResponseEntity.badRequest().body(new ApiResponse(false, "Guest already exists"));
//         }

//         Guest guest = new Guest();
//         guest.setName(dto.getName());
//         guest.setEmail(dto.getEmail());
//         guest.setPassword(passwordEncoder.encode(dto.getPassword()));
//         guest.setRole(Role.GUEST);

//         guestRepository.save(guest);
//         return ResponseEntity.ok().body(new ApiResponse(true, "Guest registered successfully"));
//     }

//     public ResponseEntity<ApiResponse> guestLogin(GuestLoginDTO dto){
//         Optional<Guest> guestOpt = guestRepository.findByEmail(dto.getEmail());

//         if(guestOpt.isEmpty()){
//             return ResponseEntity.badRequest().body(new ApiResponse(false, "Guest not found"));
//         }

//         Guest guest = guestOpt.get();

//         if(!passwordEncoder.matches(dto.getPassword(),guest.getPassword())){
//             return ResponseEntity.badRequest().body(new ApiResponse(false, "Email or password Invalid"));
//         }

//         String token = jwtUtil.generateTokenForGuest(dto.getEmail(),Role.GUEST);
//         JwtResponseDTO jwtResponse = new JwtResponseDTO(token);

//         return ResponseEntity.ok().body(new ApiResponse(true, "Login Successful", jwtResponse));
//     }
// }
