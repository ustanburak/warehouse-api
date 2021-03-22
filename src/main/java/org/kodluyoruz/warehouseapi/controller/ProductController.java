package org.kodluyoruz.warehouseapi.controller;


import lombok.RequiredArgsConstructor;
import org.kodluyoruz.warehouseapi.config.SwaggerClient;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;
import org.kodluyoruz.warehouseapi.service.ProductCRUDService;
import org.kodluyoruz.warehouseapi.service.ProductsOperationService;
import org.kodluyoruz.warehouseapi.service.StockOperationService;
import org.kodluyoruz.warehouseapi.service.WarehouseCRUDService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;




@Controller
@RequiredArgsConstructor
@SwaggerClient
@RequestMapping("/products")
public class ProductController {

    private final ProductCRUDService productCRUDService;
    private final ProductsOperationService productsOperationService;
    private final StockOperationService stockOperationService;
    private final WarehouseCRUDService warehouseCRUDService;

    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("listOfProducts", productCRUDService.list().getResponseData());
        return "product/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("statuses", StatusEnum.values());
        return "product/add_form";
    }

    @PostMapping
    public ResponseEntity<WarehouseAPIResponseHolder<ProductDTO>> create(@RequestBody ProductDTO productDTO) {
        return productCRUDService.create(productDTO);
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@ModelAttribute("product") @PathVariable Long id, Model model) {
        model.addAttribute("product", productCRUDService.getById(id).getResponseData());
        model.addAttribute("statuses", StatusEnum.values());
        return "product/update_form";
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseAPIResponseHolder<ProductDTO>> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return productCRUDService.update(id, productDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WarehouseAPIResponseHolder<?>> delete(@PathVariable Long id) {
        return productCRUDService.delete(id);
    }

    @GetMapping("/{id}/information")
    public String getInformationByProductId(@PathVariable Long id, Model model) {
        model.addAttribute("product", productCRUDService.getById(id).getResponseData());
        return "product/information";
    }

    @GetMapping("/{id}/summaries")
    public String getSummariesByProductId(@PathVariable Long id, Model model) {
        model.addAttribute("product", productCRUDService.getById(id).getResponseData());
        return "product/summaries";
    }

    @GetMapping("/{id}/add-to-warehouse")
    public String addToWarehouse(@PathVariable Long id, Model model) {
        model.addAttribute("product", productCRUDService.getById(id).getResponseData());
        model.addAttribute("warehouses", warehouseCRUDService.list().getResponseData());
        return "product/add_to_warehouse_form";
    }

    //TODO servise bağlanacak.
    //Endpointleri yazıldı.
    @PutMapping("/{id}/add-to-warehouse")
    public ResponseEntity<WarehouseAPIResponseHolder<ProductDTO>> addToWarehouse(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(productDTO, HttpStatus.OK));

    }
}