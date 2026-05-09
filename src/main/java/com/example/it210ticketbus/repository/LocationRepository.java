// === FILE: com/example/it210ticketbus/repository/LocationRepository.java ===
package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByProvinceCode(String provinceCode);

    List<Location> findAllByOrderByName();

    boolean existsByProvinceCode(String provinceCode);
}
