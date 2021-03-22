package org.kodluyoruz.warehouseapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.converter.UserDTOToUserEntityConverter;
import org.kodluyoruz.warehouseapi.dao.UserRepository;
import org.kodluyoruz.warehouseapi.model.dto.UserDTO;
import org.kodluyoruz.warehouseapi.model.entites.MyUserDetails;
import org.kodluyoruz.warehouseapi.model.entites.UserEntity;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;
import org.kodluyoruz.warehouseapi.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDTOToUserEntityConverter userDTOToUserEntityConverter;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserEntity save(UserDTO userDTO) {
        UserEntity user = userDTOToUserEntityConverter.convert(userDTO);
        String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setStatus(StatusEnum.ACTIVE);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if (userEntity == null){
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new MyUserDetails(userEntity);
    }
}