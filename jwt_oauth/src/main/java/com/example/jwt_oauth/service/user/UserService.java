package com.example.jwt_oauth.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;

import javax.validation.constraints.Email;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jwt_oauth.domain.dto.UserDto;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.domain.user.UserEnum;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    

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
        
        User newUser = userRepository.save(user);
        if(newUser==null){
            //Error
            // return ResponseEntity.ok();
        }
        return ResponseEntity.ok(userDto);
    }

    public ResponseEntity<UserDto> read(String userEmail){
        User findUser = userRepository.findByEmail(userEmail).orElseThrow( () -> new UsernameNotFoundException("not found user"));        
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
                                    .build();   
        return ResponseEntity.ok(userDto);
    }

    public ResponseEntity<UserDto> update(UserDto user){
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow( () -> new UsernameNotFoundException("not found user"));

        // UserDto userDto = UserDto.builder()
        //                             .name(findUser.getName())
        //                             .email(findUser.getEmail())
        //                             .useyn(findUser.getUseyn())
        //                             .build();
        // userDto.setName(user.getName());
        // userDto.setEmail(user.getEmail());
        // userDto.setUseyn(userDto.getUseyn());
        UserDto dto = toDto(findUser);
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setUseyn(user.getUseyn());
        
        userRepository.save(toUser(dto));

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<UserDto> delete(String userEmail){
        User findUser = userRepository.findByEmail(userEmail).orElseThrow( () -> new UsernameNotFoundException("not found user"));
        
        UserDto userDto = UserDto.builder()
                                    .name(findUser.getName())
                                    .email(findUser.getEmail())
                                    .useyn(UserEnum.N)
                                    .build();
        // userRepository.delete(findUser);
        
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
                        .name(user.getName())
                        .email(user.getEmail())
                        .useyn(user.getUseyn())
                        .build();
    }

    

    
}
