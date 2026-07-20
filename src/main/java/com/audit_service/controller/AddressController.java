package com.audit_service.controller;

import com.audit_service.model.Address;
import com.audit_service.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    AddressService addressService;

    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressService.getAddressById(id);
        return address.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<Address>> getAddressesByCountry(@PathVariable String country) {
        List<Address> addresses = addressService.getAddressesByCountry(country);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/postal-code/{postalCode}")
    public ResponseEntity<List<Address>> getAddressesByPostalCode(@PathVariable String postalCode) {
        List<Address> addresses = addressService.getAddressesByPostalCode(postalCode);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        try {
            Address updatedAddress = addressService.updateAddress(id, address);
            return ResponseEntity.ok(updatedAddress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

SELECT COUNT(1)
FROM IPA_PAYMENT A,
     IPA_ISSUE B,
     CCLIENT_CONT_STDPAY D,
     CCLIENT_CONT_ROLE F,
     TAGNCYTRADE E,
     TAGNTRD_SETTLEMENT G
WHERE D.ROLE_NO = F.ROLE_NO
  AND F.ROLE_CODE = 'ISSUER_PROGRAM'
  AND B.ISSR_CLIENT_REFNO = F.CLIENT_REFNO
  AND DECODE(D.PRD_TYP_CODE, 'ALL', 'ECP', D.PRD_TYP_CODE) = E.ATRD_TYP_CODE
  AND B.ISSUE_ID = A.ISSUE_ID
  AND B.ISSUE_ID = E.ISSUE_ID
  AND A.ATRD_REFNO = E.ATRD_REFNO
  AND E.ATRD_REFNO = G.ATRD_REFNO
  AND D.PAY_CCY = G.CCYCODE
  AND A.STATUS IN ('AUTH','UNAU','STAU')
  AND D.ROLE_NO = '0000015917'
  AND D.PAY_CCY = 'USD'
  AND E.ATRD_TYP_CODE IN ('ECP','MTN')
  AND A.IS_STANDARD_PAY = 'Y';