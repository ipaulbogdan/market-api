package com.idorasi.marketapi.repo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.idorasi.marketapi.model.Product;
import com.idorasi.marketapi.model.ProductType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByPublicId(UUID publicId);

    List<Product> findAllByTypeIn(Set<ProductType> types, Sort sort);

}
