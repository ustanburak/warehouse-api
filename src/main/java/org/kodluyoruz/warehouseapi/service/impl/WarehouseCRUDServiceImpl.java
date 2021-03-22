package org.kodluyoruz.warehouseapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseError;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.converter.WarehouseDTOToWarehouseEntityConverter;
import org.kodluyoruz.warehouseapi.converter.WarehouseEntityToWarehouseDTOConverter;
import org.kodluyoruz.warehouseapi.dao.WarehouseCRUDRepository;
import org.kodluyoruz.warehouseapi.model.dto.WarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseEntity;
import org.kodluyoruz.warehouseapi.service.WarehouseCRUDService;
import org.kodluyoruz.warehouseapi.service.WarehouseOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class WarehouseCRUDServiceImpl implements WarehouseCRUDService {

    private final WarehouseCRUDRepository warehouseCRUDRepository;
    private final WarehouseOperationService warehouseOperationService;
    private final WarehouseEntityToWarehouseDTOConverter warehouseEntityToWarehouseDTOConverter;
    private final WarehouseDTOToWarehouseEntityConverter warehouseDTOToWarehouseEntityConverter;


    @Override
    public WarehouseAPIResponseHolder<Collection<WarehouseDTO>> list() {
        Collection<WarehouseEntity> warehouseEntities = warehouseCRUDRepository.list();
        if (CollectionUtils.isEmpty(warehouseEntities)) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND, WarehouseAPIResponseError
                    .builder()
                    .code("DATA_NOT_FOUND")
                    .message("No records found in the database.")
                    .build());
        }
        List<WarehouseDTO> warehouseDTOList = warehouseEntities
                .stream()
                .map(warehouseEntityToWarehouseDTOConverter::convert)
                .collect(Collectors.toList());
        return new WarehouseAPIResponseHolder<>(warehouseDTOList, HttpStatus.OK);
    }

    @Override
    public WarehouseAPIResponseHolder<WarehouseDTO> getById(Long id) {
        WarehouseEntity warehouseEntity = warehouseCRUDRepository.getById(id);
        if (warehouseEntity == null) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND, WarehouseAPIResponseError
                    .builder()
                    .code("DATA_NOT_FOUND")
                    .message("No record found in the database.")
                    .build());
        }
        WarehouseDTO warehouseDTO = warehouseEntityToWarehouseDTOConverter.convert(warehouseEntity);
        return new WarehouseAPIResponseHolder<>(warehouseDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WarehouseAPIResponseHolder<WarehouseDTO>> create(WarehouseDTO warehouseDTO) {

        if (Objects.isNull(warehouseDTO)) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("400")
                    .message("Bilgileri kontrol ediniz.")
                    .build()));
        }


        if (warehouseDTO.getName().isEmpty() || warehouseDTO.getCode().isEmpty()) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("400")
                    .message("Name ve code alanları boş olamaz.")
                    .build()));
        }

        boolean isWarehouseCodeExist = warehouseOperationService.hasExistSameCode(warehouseDTO.getCode());

        // bunları validatorlerle yapmalı
        if (isWarehouseCodeExist) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("400")
                    .message("You can not insert with same Warehouse Code")
                    .build()));
        }

        WarehouseEntity warehouseEntity = warehouseDTOToWarehouseEntityConverter.convert(warehouseDTO);
        warehouseCRUDRepository.create(warehouseEntity); // create ettikten sonra aşağıda geri döndürmek için tekrar DTO' ya çevireceğiz

        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(warehouseEntityToWarehouseDTOConverter
                .convert(warehouseEntity), HttpStatus.OK));

    }

    @Override
    public ResponseEntity<WarehouseAPIResponseHolder<WarehouseDTO>> update(Long id, WarehouseDTO data) {

        // id için set işlemi
        data.setId(id);

        // kayıtlı herhangi bir depo var mı? yoksa zaten update işlemi yapılamaz
        boolean isAnyWarehouseExists = warehouseOperationService.isThereAnyOfThis();
        if (!isAnyWarehouseExists) {

            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("NO_DATA")
                    .message("Sorry,There is no warehouse.")
                    .build()));
        }

        // status deleted seçilmişse ve depoda ürün varsa işlemi gerçekleştirme
        String warehouseStatus = data.getStatus().toString();
        if (warehouseStatus.equals("DELETED")) {
            // ürün var mı kontrolü yapıyoruz
            boolean thereAnyProductForThisWarehouse = warehouseOperationService.isThereAnyProductForThisWarehouse(data.getId());
            if (thereAnyProductForThisWarehouse) {
                return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                        .builder()
                        .code("WAREHOUSE_HAVE_PRODUCTS")
                        .message("Sorry, you can't delete this warehouse because it contains products. Please transfer the products first.")
                        .build()));

            }
        }

        // güncelleme yaparken depo codu değiştirilirse ve aynı kod ile kayıtlı başka bir depo varsa kaydı güncelleme
        boolean isExist = warehouseOperationService.hasExistSameCodeAndId(data.getId(), data.getCode());
        if (isExist) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("DUPLICATE_DATA")
                    .message("Sorry, you can't update this record. There is another record with this code.")
                    .build()));
        }

        WarehouseEntity updateEntity = warehouseDTOToWarehouseEntityConverter.convert(data);
        updateEntity.setUpdatedAt(new Date());
        WarehouseEntity updatedEntity = warehouseCRUDRepository.update(updateEntity);


        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(warehouseEntityToWarehouseDTOConverter.convert(updatedEntity),
                HttpStatus.OK));
    }

    @Override
    public ResponseEntity<WarehouseAPIResponseHolder<?>> delete(Long id) {
        // ürün kontrolü yapıyoruz

        boolean thereAnyProductForThisWarehouse = warehouseOperationService.isThereAnyProductForThisWarehouse(id);
        if (thereAnyProductForThisWarehouse) {

            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WAREHOUSE_HAVE_PRODUCTS")
                    .message("Sorry, you can't delete this warehouse because it contains products. Please transfer the products first.")
                    .build()));
        }

        warehouseCRUDRepository.delete(id);

        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(HttpStatus.OK));
    }
}