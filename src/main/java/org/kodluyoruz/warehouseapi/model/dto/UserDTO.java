package org.kodluyoruz.warehouseapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;

@Getter
@Setter
public class UserDTO extends BaseIDDTO{
    private String code;
    private String username;
    private String email;
    private String password;
    private StatusEnum status;

}