package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.UserDTO;
import org.kodluyoruz.warehouseapi.model.entites.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class UserDTOToUserEntityConverter implements WarehouseAPIConverter<UserDTO, UserEntity> {
    @Override
    public UserEntity convert(UserDTO input) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(input.getId());
        userEntity.setCode(input.getCode());
        userEntity.setUsername(input.getUsername());
        userEntity.setPassword(input.getPassword());
        userEntity.setEmail(input.getEmail());
        userEntity.setStatus(input.getStatus());
        userEntity.setCreatedAt(Objects.isNull(input.getCreatedAt()) ? new Date() : input.getCreatedAt());
        userEntity.setUpdatedAt(input.getUpdatedAt());

        return userEntity;
    }
}