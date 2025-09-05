package com.project.ecom.controllers;

import com.project.ecom.model.Address;
import com.project.ecom.payload.AddressDTO;
import com.project.ecom.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @PostMapping("/address")   // create address for logged-in user
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }


    @GetMapping("/addresses")   // only admin can see all addresses
    public ResponseEntity<List<AddressDTO>> getAllAddress() {
        return ResponseEntity.ok(addressService.getAllAddress());
    }


    @GetMapping("/address/id/{addressId}")  // only admin can get any address by id
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return ResponseEntity.ok(addressDTO);
    }

    @GetMapping("/address/user")  // to get logged in user's addresses
    public ResponseEntity<List<AddressDTO>> getAddressesByUserId() {
        List<AddressDTO> addresses = addressService.getLoggedInUserAddresses();
        return ResponseEntity.ok(addresses);
    }

    @PatchMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }


    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        return new ResponseEntity<>(addressService.deleteAddress(addressId), HttpStatus.OK);
//        return ResponseEntity.noContent().build();
    }
}
