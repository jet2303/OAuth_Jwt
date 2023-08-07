package com.example.jwt_oauth.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;

import javax.validation.constraints.Email;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_oauth.domain.dto.UserDto;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.domain.user.UserEnum;
import com.example.jwt_oauth.payload.error.CustomException;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.error.errorCodes.UserErrorCode;
import com.example.jwt_oauth.payload.request.auth.ChangePasswordRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.Message;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // public ResponseEntity<?> readByUser(UserPrincipal userPrincipal){
    //     Optional<User> user = userRepository.findById(userPrincipal.get)
    // }
    
    // public ResponseEntity<?> readByEmail(String email){
    //     Optional<User> user = userRepository.findByEmail(email);

    //     return ResponseEntity.ok(user);
    // }

    public ResponseEntity<UserDto> create(UserDto userDto){
        
        User user = User.builder()
                            .name(userDto.getName())
                            .email(userDto.getEmail())
                            .useyn(UserEnum.Y)
                            .imageUrl(userDto.getImageUrl())
                            .emailVerified(null)
                            .password(userDto.getPassword())
                            .provider(userDto.getProvider())
                            .providerId(userDto.getProviderId())
                            .role(userDto.getRole())
                            .build();   
        
        if(userRepository.existsByEmail(user.getEmail())){
            // return ResponseEntity.ok(ApiResponse.builder()
            //                                     .check(false)
            //                                     .information(Message.builder()
            //                                                             .message("중복된 계정이 존재")
            //                                                             .build()
            //                                     )                                   
            //                                     .build());
            return ResponseEntity.ok(UserDto.builder().build());
        } 

        User newUser = userRepository.save(user);
        if(newUser==null){
            //Error
            // return ResponseEntity.ok();
        }
        return ResponseEntity.ok(userDto);
    }

    public ResponseEntity<UserDto> read(String userEmail){
        User findUser = userRepository.findByEmail(userEmail).orElseThrow( () -> new CustomException());
        // User findUser = userRepository.findByEmail(userEmail).orElseThrow( () -> new UsernameNotFoundException("not found user"));        
        UserDto userDto = UserDto.builder()
                                    .id(findUser.getId())
                                    .name(findUser.getName())
                                    .email(findUser.getEmail())
                                    .useyn(findUser.getUseyn())
                                    .imageUrl(findUser.getImageUrl())
                                    .emailVerified(null)
                                    .password(findUser.getPassword())
                                    .provider(findUser.getProvider())
                                    .providerId(findUser.getProviderId())
                                    .role(findUser.getRole())
                                    .createdDate(findUser.getCreatedDate())
                                    .build();   
        return ResponseEntity.ok(userDto);
    }

    //password update, name update 분리할것.
    public ResponseEntity<ApiResponse> update(UserDto userDto){
        User findUser = userRepository.findByEmail(userDto.getEmail())
                                        .orElseThrow( () -> new UsernameNotFoundException("not found user"));

        // 프론트에서 password가 null값으로 올 경우 "0000" 으로 치환해야함.
        // update 가능한 조건을 추가.
        if(userDto.getPassword() == null){
            userDto.setPassword("0000");
        }else if(userDto.getPassword().length() < 4 ){
            userDto.setPassword("0000");
        }else{
            String encryptPw = passwordEncoder.matches(userDto.getPassword(), findUser.getPassword()) ? findUser.getPassword() : passwordEncoder.encode(userDto.getPassword());

            Integer result = userRepository.userInfoUpdate(userDto.getName(), encryptPw, findUser.getEmail());
            
            if(result.intValue() == 1){
                return ResponseEntity.ok(ApiResponse.builder()
                                                    .check(true)
                                                    .newInformation(Message.builder()
                                                                            .message("수정 성공")
                                                                            .build())
                                                    .build());
            }
        }
        
        return ResponseEntity.ok(ApiResponse.builder()
                                            .check(true)
                                            .newInformation(Message.builder()
                                                                    .message("수정 실패")
                                                                    .build())
                                            .build());
    }
    
    @Transactional
    @Modifying
    public ResponseEntity<ApiResponse> update_pw(ChangePasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                                    .orElseThrow(() -> new RestApiException(UserErrorCode.NOT_FOUND_USER));
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RestApiException(UserErrorCode.NOT_MATCHED_PASSWORD);
        }
        
        Integer result = userRepository.userInfoUpdate(user.getName(), passwordEncoder.encode(request.getNewPassword()) , user.getEmail());
        if(result == 1){
            return ResponseEntity.ok(ApiResponse.builder()
                                                .check(true)
                                                .newInformation(Message.builder()
                                                                        .message("수정 성공")
                                                                        .build())
                                                .build());
        }
        return ResponseEntity.ok(ApiResponse.builder()
                                            .check(true)
                                            .newInformation(Message.builder()
                                                                    .message("수정 실패")
                                                                    .build())
                                            .build());
    }
    

    public ResponseEntity<UserDto> delete(String userEmail){
        User findUser = userRepository.findByEmail(userEmail)
                                        .orElseThrow( () -> new RestApiException(UserErrorCode.NOT_FOUND_USER));
        
        UserDto userDto = UserDto.builder()
                                    .name(findUser.getName())
                                    .email(findUser.getEmail())
                                    .useyn(UserEnum.N)
                                    .build();
        
        return ResponseEntity.ok(userDto);
    }

    private User toUser(UserDto userDto){

        return User.builder()
                    .name(userDto.getName())
                    .email(userDto.getEmail())
                    .useyn(userDto.getUseyn())
                    .build();
    }




    private UserDto toDto(User user){
        return UserDto.builder()
                        .email(user.getEmail())
                        .build();
    }

    private boolean validPwChk(String password){
        // 4자리 이상
        if(password.length() < 4)
            return false;
        
        return false;
    }

    
}
