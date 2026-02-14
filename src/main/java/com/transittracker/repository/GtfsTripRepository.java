package com.transittracker.repository;

import com.transittracker.entity.GtfsTrip;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GtfsTripRepository extends CrudRepository<GtfsTrip, String> {
}
