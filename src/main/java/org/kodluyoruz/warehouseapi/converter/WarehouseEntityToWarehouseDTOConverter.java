package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.WarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseEntity;
import org.springframework.stereotype.Component;

@Component
public class WarehouseEntityToWarehouseDTOConverter implements
        WarehouseAPIConverter<WarehouseEntity, WarehouseDTO> {
    @Override
    public WarehouseDTO convert(WarehouseEntity input) {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(input.getId());
        warehouseDTO.setName(input.getName());
        warehouseDTO.setCode(input.getCode());
        warehouseDTO.setStatus(input.getStatus());
        warehouseDTO.setCreatedAt(input.getCreatedAt());
        warehouseDTO.setUpdatedAt(input.getUpdatedAt());
        return warehouseDTO;
    }
}
