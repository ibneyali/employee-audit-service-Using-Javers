package com.audit_service.repository;

import com.audit_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    String VALIDATE_STANDARD_PAYMENT_PRODUCT_QUERY = """
SELECT CASE
         WHEN COUNT(*) > 0 THEN 'TRUE'
         ELSE 'FALSE'
       END AS PAYMENT_EXISTS
FROM ATISADMIN.IPA_PAYMENT A,
     ATISADMIN.IPA_ISSUE B,
     STATICDATA.IPA_CONTACT_DET C,
     ATISADMIN.TAGNCYTRADE D,
     ATISADMIN.TAGNTRD_SETTLEMENT E,
     STATICDATA.LNT_STL_INS P,
     STATICDATA.CCLIENT_CONT_ROLE R
WHERE A.ISSUE_ID = B.ISSUE_ID
  AND A.ATRD_REFNO = D.ATRD_REFNO
  AND B.ISSUE_ID = D.ISSUE_ID
  AND D.ATRD_REFNO = E.ATRD_REFNO
  AND B.ISSUE_ID = C.ISSUE_ID
  AND P.ROLE_NO = C.ROLE_NO
  AND C.ROLE_NO = R.ROLE_NO
  AND P.ROLE_NO = R.ROLE_NO
  AND R.ROLE_CODE = 'ISSUER_PROGRAM'
  AND D.ATRD_TYP_CODE = P.PRDCT_TYP_CD
  AND E.CCYCODE = P.PYMT_CCY_CD
  AND P.ROLE_NO = :roleNo
  AND P.PYMT_CCY_CD = :currency
  AND P.PRDCT_TYP_CD = :productType
  AND A.STATUS IN ('AUTH','UNAU')
""";

    String VALIDATE_STANDARD_PAYMENT_ALL_QUERY = """
SELECT CASE
         WHEN COUNT(*) > 0 THEN 'TRUE'
         ELSE 'FALSE'
       END AS PAYMENT_EXISTS
FROM ATISADMIN.IPA_PAYMENT A,
     ATISADMIN.IPA_ISSUE B,
     STATICDATA.IPA_CONTACT_DET C,
     ATISADMIN.TAGNCYTRADE D,
     ATISADMIN.TAGNTRD_SETTLEMENT E,
     STATICDATA.LNT_STL_INS P,
     STATICDATA.CCLIENT_CONT_ROLE R
WHERE A.ISSUE_ID = B.ISSUE_ID
  AND A.ATRD_REFNO = D.ATRD_REFNO
  AND B.ISSUE_ID = D.ISSUE_ID
  AND D.ATRD_REFNO = E.ATRD_REFNO
  AND B.ISSUE_ID = C.ISSUE_ID
  AND P.ROLE_NO = C.ROLE_NO
  AND P.ROLE_NO = R.ROLE_NO
  AND C.ROLE_NO = R.ROLE_NO
  AND R.ROLE_CODE = 'ISSUER_PROGRAM'
  AND D.ATRD_TYP_CODE IN ('ECP','MTN')
  AND E.CCYCODE = P.PYMT_CCY_CD
  AND P.ROLE_NO = :roleNo
  AND P.PYMT_CCY_CD = :currency
  AND P.PRDCT_TYP_CD = :productType
  AND A.STATUS IN ('AUTH','UNAU')
""";

    List<Address> findByCountry(String country);

    List<Address> findByPostalCode(String postalCode);
}


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClientStlInstructionRequest {

    private String ebaClrgIn;
    private String tgt2StlIn;
    private String fedwirePrcsIn;
    private String miftCompltdIn;

    private String intmBankNm;
    private String intmBankSortCd;
    private String intmBankAccNb;
    private String intmBankSwiftCd;
    private String intmBankAbaRtNb;

    private String benfBankNm;
    private String benfBankSortCd;
    private String benfBankAccNb;
    private String benfBankSwiftCd;
    private String benfBankAbaRtNb;

    private String benfNm;
    private String benfAddrTx;
    private String benfAccNb;
    private String benfSwiftCd;
    private String benfAbaRtNb;

    private String swiftInfoTx;
    private String ordgCustAccNb;
    private String ordgCustAddrLine1Tx;
    private String ordgCustAddrLine2Tx;
    private String ordgCustAddrLine3Tx;
    private String ordgCustAddrLine4Tx;

    private String ordgFinInstSwiftCd;
    private String remitDtlsTx;
    private String chgBrrCd;

    private String mkrUsrId;
    private LocalDate mkrDt;

    private String chkrUsrId;
    private LocalDate chkrDt;

    private String prcsStsCd;
    private LocalDateTime updTs;

    private String partAuthUsrId;
    private LocalDateTime partAuthTs;
}