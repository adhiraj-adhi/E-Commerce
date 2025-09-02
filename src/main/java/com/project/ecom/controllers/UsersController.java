package com.project.ecom.controllers;

import com.project.ecom.dao.UserRepository;
import com.project.ecom.model.Address;
import com.project.ecom.model.AppRole;
import com.project.ecom.model.Role;
import com.project.ecom.model.User;
import com.project.ecom.payload.LoginRequestDTO;
import com.project.ecom.payload.LoginResponseDTO;
import com.project.ecom.payload.RegisterRequestDTO;
import com.project.ecom.security.UserDetailsServiceImpl;
import com.project.ecom.security.jwt.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class UsersController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO requestDTO){
        String username = requestDTO.getUsername();
        String password = passwordEncoder.encode(requestDTO.getPassword());


        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Address address = new Address();
        address.setBuildingName(requestDTO.getBuildingName());
        address.setStreet(requestDTO.getStreet());
        address.setCity(requestDTO.getCity());
        address.setState(requestDTO.getState());
        address.setCountry(requestDTO.getCountry());
        address.setZipcode(requestDTO.getZipcode());
        List<User> userList = new ArrayList<>();
        userList.add(user);
        address.setUsers(userList);

        List<Address> addressList = new ArrayList<>();
        addressList.add(address);

        user.setAddresses(addressList);

        boolean isEmpty = userRepository.count() == 0;  // check if this is first element
        if (isEmpty) {
            Set<Role> roles = new HashSet<>();

            Role userRole = new Role();
            userRole.setRole(AppRole.ROLE_USER);
            userRole.setUser(user);

            Role adminRole = new Role();
            adminRole.setRole(AppRole.ROLE_ADMIN);
            adminRole.setUser(user);

            roles.add(userRole);
            roles.add(adminRole);
            user.setRoles(roles);
        } else {

            Set<Role> role = new HashSet<>();

            Role userRole = new Role();
            userRole.setRole(AppRole.ROLE_USER);
            userRole.setUser(user);

            //        To create a seller
            //        Role sellerRole = new Role();
            //        sellerRole.setRole(AppRole.ROLE_SELLER);
            //        sellerRole.setUser(user);
            //        role.add(sellerRole);

            role.add(userRole);
            user.setRoles(role);
        }
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDTO requestDTO) {
        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails!=null && passwordEncoder.matches(password, userDetails.getPassword())) {
            String jwt = jwtUtils.generateJWTTokenFromUsername(userDetails);
            LoginResponseDTO responseDTO = new LoginResponseDTO(jwt, "Login Successful");

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
