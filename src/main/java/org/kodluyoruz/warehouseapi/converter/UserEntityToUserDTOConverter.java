package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.UserDTO;
import org.kodluyoruz.warehouseapi.model.entites.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class UserEntityToUserDTOConverter implements WarehouseAPIConverter<UserEntity, UserDTO> {
    @Override
    public UserDTO convert(UserEntity input) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(input.getId());
        userDTO.setCode(input.getCode());
        userDTO.setUsername(input.getUsername());
        userDTO.setPassword(input.getPassword());
        userDTO.setEmail(input.getEmail());
        userDTO.setStatus(input.getStatus());
        userDTO.setCreatedAt(Objects.isNull(input.getCreatedAt()) ? new Date() : input.getCreatedAt());
        userDTO.setUpdatedAt(input.getUpdatedAt());

        return userDTO;
    }
}