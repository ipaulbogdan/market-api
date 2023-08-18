package com.idorasi.marketapi.model;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "transactions")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends BaseEntity {

    private UUID publicId;
    @ManyToOne
    private Product product;
    private Long quantity;
    private String buyerUsername;
    private String sellerUsername;

}
