package com.project.ecom.services;

import com.project.ecom.dao.AddressRepository;
import com.project.ecom.dao.UserRepository;
import com.project.ecom.exceptions.ResourceNotFoundException;
import com.project.ecom.model.Address;
import com.project.ecom.model.User;
import com.project.ecom.payload.AddressDTO;
import com.project.ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User loggerdInUser = authUtil.loggedInUser();

        Address address = new Address();
        address.setBuildingName(addressDTO.getBuildingName());
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setZipcode(addressDTO.getZipcode());
        address.setUser(loggerdInUser);

        Address savedAddress = addressRepository.save(address);

        List<Address> addresses = loggerdInUser.getAddresses();
        addresses.add(savedAddress);
        loggerdInUser.setAddresses(addresses);

        userRepository.save(loggerdInUser);

        addressDTO.setAddressId(savedAddress.getAddressId());
        return addressDTO;
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        return addressRepository.findAll().stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        return modelMapper.map(addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", addressId, "addressId")), AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getLoggedInUserAddresses() {
        User loggedInUser = authUtil.loggedInUser();
        return loggedInUser.getAddresses().stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        // if the address belongs to logged-in user, then only update
        User loggedInUser = authUtil.loggedInUser();
        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("Address", addressId, "addressId"));
        if(address.getUser().getUserId().equals(loggedInUser.getUserId())) {
            address.setBuildingName(addressDTO.getBuildingName());
            address.setStreet(addressDTO.getStreet());
            address.setCity(addressDTO.getCity());
            address.setState(addressDTO.getState());
            address.setCountry(addressDTO.getCountry());
            address.setZipcode(addressDTO.getZipcode());
            addressRepository.save(address);
        } else {
            throw new ResourceNotFoundException("Address", addressId, "addressId");
        }
        addressDTO.setAddressId(addressId);
        return addressDTO;
    }

    @Override
    public String deleteAddress(Long addressId) {
        User loggedInUser = authUtil.loggedInUser();
        if (loggedInUser.getAddresses().stream().filter(address -> address.getAddressId().equals(addressId)).toList().isEmpty()) {
            throw new ResourceNotFoundException("Address", addressId, "addressId");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", addressId, "addressId"));

        loggedInUser.getAddresses().remove(address);
        userRepository.save(loggedInUser);
        addressRepository.delete(address);
        return "Address with id " + addressId + " deleted successfully!";
    }
}
