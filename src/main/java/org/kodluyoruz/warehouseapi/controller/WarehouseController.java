package org.kodluyoruz.warehouseapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.config.SwaggerClient;
import org.kodluyoruz.warehouseapi.model.dto.WarehouseDTO;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;
import org.kodluyoruz.warehouseapi.service.WarehouseCRUDService;
import org.kodluyoruz.warehouseapi.service.WarehouseOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@SwaggerClient
@RequestMapping("/warehouses")
@Controller
@Slf4j
public class WarehouseController {

    private final WarehouseCRUDService warehouseCRUDService;
    private final WarehouseOperationService warehouseOperationService;

    @GetMapping
    public String getAllWarehouses(Model model) {
        model.addAttribute("listOfWarehouses", warehouseCRUDService.list().getResponseData());
        return "warehouse/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("statuses", StatusEnum.values());
        return "warehouse/add_form";
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid WarehouseDTO warehouseDTO) {
        return warehouseCRUDService.create(warehouseDTO);
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@ModelAttribute("warehouse") @PathVariable Long id, Model model) {
        model.addAttribute("warehouse", warehouseCRUDService.getById(id).getResponseData());
        model.addAttribute("statuses", StatusEnum.values());
        return "warehouse/update_form";
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@ModelAttribute("warehouse") @PathVariable Long id, @RequestBody WarehouseDTO warehouseDTO) {
        return warehouseCRUDService.update(id, warehouseDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return warehouseCRUDService.delete(id);
    }

    @PutMapping(path = "/transfer/{fromWarehouseId}/{toWarehouseId}")
    public WarehouseAPIResponseHolder<?> transferAllProducts
            (@PathVariable("fromWarehouseId") Long fromWarehouseId, @PathVariable("toWarehouseId") Long toWarehouseId) {
        return warehouseOperationService.transferAllProducts(fromWarehouseId, toWarehouseId);
    }

    @GetMapping("/{id}/products")
    public String getAllProductsByWarehouseId(@PathVariable Long id, Model model) {
        model.addAttribute("warehouse", warehouseCRUDService.getById(id).getResponseData());
        model.addAttribute("listOfWarehouseProducts", warehouseOperationService.getProductsByWarehouseId(id).getResponseData());
        return "warehouse/warehouse_products";
    }

    @GetMapping("/{id}/information")
    public String getInformationByProductId(@PathVariable Long id, Model model) {
        model.addAttribute("warehouse", warehouseCRUDService.getById(id).getResponseData());
        return "warehouse/information";
    }

    @GetMapping("/{id}/summaries")
    public String getSummariesByWarehouseId(@PathVariable Long id, Model model) {
        model.addAttribute("warehouse", warehouseCRUDService.getById(id).getResponseData());
        return "warehouse/summaries";
    }

}