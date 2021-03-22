package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.dto.BaseIDDTO;

public interface WarehouseAndProductOperationBaseService<T extends BaseIDDTO> {

    boolean hasExistSameCode(String code);

    boolean hasExistSameCodeAndId(Long id, String code);

    boolean isThereAnyOfThis();

    boolean isThereAnyActiveEntryAtThisId(Long id);

}