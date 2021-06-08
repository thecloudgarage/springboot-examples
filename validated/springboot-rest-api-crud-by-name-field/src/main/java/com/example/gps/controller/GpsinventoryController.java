package com.example.gps.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gps.exception.ResourceNotFoundException;
import com.example.gps.model.Gpsinventory;
import com.example.gps.repository.GpsinventoryRepository;

@RestController
@RequestMapping("/api/v1")
public class GpsinventoryController {
    @Autowired
    private GpsinventoryRepository gpsinventoryRepository;

    @GetMapping("/gpsinventory")
    public List<Gpsinventory> getAllGpsinventory() {
        return gpsinventoryRepository.findAll();
    }

    @GetMapping("/gpsinventory/{gpsid}")
    public ResponseEntity<Gpsinventory> getGpsinventoryByGpsterminalId(@PathVariable(value = "gpsid") String gpsterminalId)
        throws ResourceNotFoundException {
        Gpsinventory gpsinventory = gpsinventoryRepository.findByGpsterminalId(gpsterminalId);
        return ResponseEntity.ok().body(gpsinventory);
    }    
    
    @PostMapping("/gpsinventory")
    public Gpsinventory createGpsinventory(@Valid @RequestBody Gpsinventory gpsinventory) {
        return gpsinventoryRepository.save(gpsinventory);
    }

    @PutMapping("/gpsinventory/{gpsid}")
    public ResponseEntity<Gpsinventory> updateGpsinventory(@PathVariable(value = "gpsid") String gpsterminalId, @Valid @RequestBody Gpsinventory gpsinventoryDetails) throws ResourceNotFoundException {
        Gpsinventory gpsinventory = gpsinventoryRepository.findByGpsterminalId(gpsterminalId);
        gpsinventory.setgpsterminalId(gpsinventoryDetails.getgpsterminalId());
	gpsinventory.setcustomerId(gpsinventoryDetails.getcustomerId());
        gpsinventory.setcustomerName(gpsinventoryDetails.getcustomerName());
        gpsinventory.setlat(gpsinventoryDetails.getlat());
	gpsinventory.setlon(gpsinventoryDetails.getlon());
        final Gpsinventory updatedGpsinventory = gpsinventoryRepository.save(gpsinventory);
        return ResponseEntity.ok(updatedGpsinventory);
    }

}
