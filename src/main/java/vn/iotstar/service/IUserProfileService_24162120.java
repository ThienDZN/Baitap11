package vn.iotstar.service;

import vn.iotstar.dto.UserProfileUpdateRequest_24162120;
import vn.iotstar.entity.UserAccount_24162120;

public interface IUserProfileService_24162120 {
    UserAccount_24162120 findById(Long userId);
    UserAccount_24162120 updateProfile(Long userId, UserProfileUpdateRequest_24162120 request);
}
