package com.tiger.repository;

import com.tiger.domain.opendate.OpenDate;
import com.tiger.domain.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OpenDateRepository extends JpaRepository<OpenDate, Long> {

    Optional<List<OpenDate>> findAllByVehicleIdOrderByStartDateAsc(Long vid);

    boolean existsByVehicleId(Long vid);
}
