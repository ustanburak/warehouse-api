package org.kodluyoruz.warehouseapi.controller;

import lombok.RequiredArgsConstructor;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.config.SwaggerClient;
import org.kodluyoruz.warehouseapi.model.dto.ProductWarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.kodluyoruz.warehouseapi.service.ProductCRUDService;
import org.kodluyoruz.warehouseapi.service.StockOperationService;
import org.kodluyoruz.warehouseapi.service.WarehouseCRUDService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
@SwaggerClient
@RequestMapping("/stocks")
public class StockController {

    private final StockOperationService stockOperationService;
    private final ProductCRUDService productCRUDService;
    private final WarehouseCRUDService warehouseCRUDService;

    @PutMapping(path = "/{productId}/transfer/{fromWarehouseId}/{toWarehouseId}")
    public WarehouseAPIResponseHolder<?> transferThisProduct(@PathVariable("productId") Long productId,
                                                             @PathVariable("fromWarehouseId") Long fromWarehouseId,
                                                             @PathVariable("toWarehouseId") Long toWarehouseId) {
        return stockOperationService.transferThisProduct(productId, fromWarehouseId, toWarehouseId);
    }

    @GetMapping("/update/{warehouseId}/{productId}")
    public String getUpdateForm(@PathVariable("productId") Long productId, @PathVariable("warehouseId") Long warehouseId,
                                Model model) {
        model.addAttribute("product", productCRUDService.getById(productId).getResponseData());
        model.addAttribute("warehouse", warehouseCRUDService.getById(warehouseId).getResponseData());
        return "product/update_stock_form";
    }

    @PostMapping("/update")
    public WarehouseAPIResponseHolder<?> updateProductStockQuantity(@RequestBody ProductWarehouseDTO productWarehouseDTO) {
        return stockOperationService.updateProductStockQuantity(productWarehouseDTO);
    }

    @GetMapping("/transfer/{warehouseId}/{productId}")
    public String getTransferForm(@PathVariable("productId") Long productId, @PathVariable("warehouseId") Long warehouseId, Model model) {
        model.addAttribute("product", productCRUDService.getById(productId).getResponseData());
        model.addAttribute("warehouse", warehouseCRUDService.getById(warehouseId).getResponseData());
        model.addAttribute("warehouses", warehouseCRUDService.list().getResponseData());
        return "product/transfer_form";
    }

    @PostMapping("/transfer/{productId}/{warehouseId}")
    public WarehouseAPIResponseHolder<?> transferProduct(@PathVariable("productId") Long productId, @PathVariable("warehouseId") Long warehouseId,  @RequestBody ProductWarehouseDTO productWarehouseDTO,Model model) {
        return stockOperationService.updateProductStockQuantity(productWarehouseDTO);
    }

    @GetMapping
    public WarehouseAPIResponseHolder<Collection<Summary>> getSummaries() {
        return stockOperationService.getSummaries();
    }

    @PostMapping("/add")
    public WarehouseAPIResponseHolder<?> addNewStock(@RequestBody ProductWarehouseDTO productWarehouseDTO) {
        return stockOperationService.addNewStock(productWarehouseDTO);
    }


}