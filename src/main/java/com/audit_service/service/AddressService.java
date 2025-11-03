package com.audit_service.service;

import com.audit_service.model.Address;
import com.audit_service.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public List<Address> getAllAddresses() {
        return (List<Address>) addressRepository.findAll();
    }

    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    public List<Address> getAddressesByCountry(String country) {
        return addressRepository.findByCountry(country);
    }

    public List<Address> getAddressesByPostalCode(String postalCode) {
        return addressRepository.findByPostalCode(postalCode);
    }

    @Transactional
    public Address createAddress(Address address) {
        address.setCreatedTimestamp(LocalDateTime.now());
        address.setUpdatedTimestamp(LocalDateTime.now());
        if (address.getUpdatedBy() == null) {
            address.setUpdatedBy("SYSTEM");
        }

        Address saved = addressRepository.save(address);

        return saved;
    }

    @Transactional
    public Address updateAddress(Long id, Address addressDetails) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        address.setAddressLine1(addressDetails.getAddressLine1());
        address.setAddressLine2(addressDetails.getAddressLine2());
        address.setAddressLine3(addressDetails.getAddressLine3());
        address.setCountry(addressDetails.getCountry());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setUpdatedTimestamp(LocalDateTime.now());
        address.setUpdatedBy(addressDetails.getUpdatedBy() != null ? addressDetails.getUpdatedBy() : "SYSTEM");

        Address saved = addressRepository.save(address);

        return saved;
    }

    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        String initiator = address.getUpdatedBy() != null ? address.getUpdatedBy() : "SYSTEM";

        addressRepository.delete(address);
    }
}
