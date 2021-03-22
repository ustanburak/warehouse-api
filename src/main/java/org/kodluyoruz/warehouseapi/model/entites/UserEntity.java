package org.kodluyoruz.warehouseapi.model.entites;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;

import javax.persistence.*;

@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Column(name = "code", unique = true, length = 50, nullable = false)
    private String code;

    @Column(name = "user_name", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 150, nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", length = 7, nullable = false)
    private StatusEnum status = StatusEnum.ACTIVE;

}