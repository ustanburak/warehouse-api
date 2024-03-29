package org.kodluyoruz.warehouseapi.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class WarehouseAPIResponseHolder<T> {

    //@JsonInclude(JsonInclude.Include.NON_NULL) // null alanlar gözükmesin diye
    private T responseData;
    //@JsonInclude(JsonInclude.Include.NON_NULL)

    private HttpStatus httpStatus;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private WarehouseAPIResponseError error;

    public WarehouseAPIResponseHolder(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public WarehouseAPIResponseHolder(T responseData, HttpStatus httpStatus) {
        this.responseData = responseData;
        this.httpStatus = httpStatus;
    }

    public WarehouseAPIResponseHolder(HttpStatus httpStatus,
                                      WarehouseAPIResponseError error) {
        this.httpStatus = httpStatus;
        this.error = error;
    }

}
