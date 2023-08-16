package com.idorasi.marketapi.model;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "retired_products")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class RetiredProduct extends BaseEntity {

    private UUID publicId;

    private String name;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private ProductType type;

    private String retirementReason;

}
