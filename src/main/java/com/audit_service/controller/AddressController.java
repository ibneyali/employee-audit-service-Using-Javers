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

  Implement a new IPA Payment validation and status update feature in the Issuance Service.

Requirement:

Create two separate APIs.

-------------------------------------------------------
1. Validation API
-------------------------------------------------------

Create a new REST endpoint:

GET /ipa-payment/validate

Request Parameters:
- roleNo
- productType (ALL, ECP, MTN)
- currency

Example:

GET /ipa-payment/validate?roleNo=0000015917&productType=ECP&currency=USD

Purpose:

Before allowing the Product Type of a Standard Payment (SSI) to be modified, validate whether any active IPA Payment already exists.

Business Rule:

If active IPA Payments exist with status

- AUTH
- UNAU
- STAU

return TRUE.

Otherwise return FALSE.

The controller must return:

ResponseEntity<Boolean>

-------------------------------------------------------
Service Layer
-------------------------------------------------------

The service should decide which repository query to execute based on the incoming productType.

Example:

if productType == "ECP"
    execute validateForEcp()

else if productType == "MTN"
    execute validateForMtn()

else
    throw IllegalArgumentException("Unsupported product type")

Repository methods should return COUNT(*).

The service converts it into boolean.

return count > 0;

-------------------------------------------------------
Repository
-------------------------------------------------------

Create two native queries.

Method 1

int validateForEcp(String roleNo, String currency);

Use this query:

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
AND DECODE(D.PRD_TYP_CODE,'ALL','ECP',D.PRD_TYP_CODE)=E.ATRD_TYP_CODE
AND B.ISSUE_ID=A.ISSUE_ID
AND B.ISSUE_ID=E.ISSUE_ID
AND A.ATRD_REFNO=E.ATRD_REFNO
AND E.ATRD_REFNO=G.ATRD_REFNO
AND D.PAY_CCY=G.CCYCODE
AND A.STATUS IN ('AUTH','UNAU','STAU')
AND D.ROLE_NO=:roleNo
AND D.PAY_CCY=:currency
AND E.ATRD_TYP_CODE IN ('ECP','MTN')
AND A.IS_STANDARD_PAY='Y';

Method 2

int validateForMtn(String roleNo, String currency);

Use this query:

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
AND D.PRD_TYP_CODE = E.ATRD_TYP_CODE
AND B.ISSUE_ID=A.ISSUE_ID
AND B.ISSUE_ID=E.ISSUE_ID
AND A.ATRD_REFNO=E.ATRD_REFNO
AND E.ATRD_REFNO=G.ATRD_REFNO
AND D.PAY_CCY=G.CCYCODE
AND A.STATUS IN ('AUTH','UNAU','STAU')
AND D.ROLE_NO=:roleNo
AND D.PAY_CCY=:currency
AND D.PRD_TYP_CODE='ECP'
AND A.IS_STANDARD_PAY='Y';

-------------------------------------------------------
2. Update IPA Payment Status API
-------------------------------------------------------

Create another REST endpoint.

PUT /ipa-payment/status

Request Body:

{
    "tradeRefNo": "...",
    "status": "AUTH"
}

Purpose:

Update only IPA_PAYMENT.STATUS.

Repository method:

int updatePaymentStatus(String tradeRefNo, String status);

Use the existing update query.

UPDATE IPA_PAYMENT
SET STATUS = :status
WHERE ATRD_REFNO = :tradeRefNo;

Return HTTP 200 when the update succeeds.

Mark the service method with @Transactional.

-------------------------------------------------------
Audit Trail
-------------------------------------------------------

After updating IPA_PAYMENT successfully, insert a record into IPA_PAYMENT_AUDIT_TRAIL using the project's existing audit mechanism (if available). Reuse the existing audit implementation rather than creating a new one.

-------------------------------------------------------
Project Structure
-------------------------------------------------------

Follow the existing project structure.

Controller
Service
ServiceImpl
Repository

Reuse the existing IpaPayment entity.

Use constructor injection.

Use Spring Data JPA native queries.

Add logging.

Add exception handling.

Do not modify existing functionality.

Keep validation and update logic completely separate.