package org.kodluyoruz.warehouseapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseError;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.dao.ProductsOperationRepository;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.kodluyoruz.warehouseapi.service.ProductsOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductsOperationServiceImpl implements ProductsOperationService {

    private final ProductsOperationRepository productsOperationRepository;

    @Override
    public boolean hasExistSameCode(String code) {
        return productsOperationRepository.hasExistSameCode(code);
    }

    @Override
    public boolean hasExistSameCodeAndId(Long id, String code) {
        return productsOperationRepository.hasExistSameCodeAndId(id, code);
    }

    @Override
    public boolean isThereAnyOfThis() {
        return productsOperationRepository.isThereAnyOfThis();
    }

    @Override
    public boolean isThereAnyProductForThisIdInStock(Long productId) {
        return productsOperationRepository.isThereAnyProductForThisIdInStock(productId);
    }

    @Override
    public WarehouseAPIResponseHolder<Collection<Summary>> getProductsAndWarehousesByProductId(Long productId) {
        boolean isWarehouseExist = isThereAnyActiveEntryAtThisId(productId);

        if (!isWarehouseExist) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WRONG_WAREHOUSE_ID")
                    .message("An active repository for these ids was not found or selected warehouses are not active. Please check the values.")
                    .build());
        }

        Collection<Summary> warehouseAndProducts = productsOperationRepository.getProductsByWarehouseId(productId);

        if (CollectionUtils.isEmpty(warehouseAndProducts)) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND, WarehouseAPIResponseError
                    .builder()
                    .code("DATA_NOT_FOUND")
                    .message("No records found in the database.")
                    .build());
        }

        return new WarehouseAPIResponseHolder<>(warehouseAndProducts,HttpStatus.OK);
    }

    @Override
    public boolean isThereAnyActiveEntryAtThisId(Long id) {
        return productsOperationRepository.isThereAnyActiveEntryAtThisId(id);
    }
}
