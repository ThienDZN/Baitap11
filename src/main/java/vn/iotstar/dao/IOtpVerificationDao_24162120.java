package vn.iotstar.dao;

import vn.iotstar.entity.OtpVerification_24162120;

public interface IOtpVerificationDao_24162120 {
    void insert(OtpVerification_24162120 otpVerification);
    void markAllUnusedAsUsed(String email, String purpose);
    OtpVerification_24162120 findLatest(String email, String purpose);
    void update(OtpVerification_24162120 otpVerification);
}
