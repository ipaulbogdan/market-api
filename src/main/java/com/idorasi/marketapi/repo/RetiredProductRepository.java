package com.idorasi.marketapi.repo;

import com.idorasi.marketapi.model.RetiredProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetiredProductRepository extends JpaRepository<RetiredProduct, Long> {
}
