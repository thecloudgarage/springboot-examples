package com.example.gps.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gps.model.Gpsinventory;

@Repository
public interface GpsinventoryRepository extends JpaRepository<Gpsinventory, Long>{
Gpsinventory findByGpsterminalId(String gpsterminalId);
}
