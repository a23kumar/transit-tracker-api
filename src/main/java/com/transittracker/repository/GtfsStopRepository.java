package com.transittracker.repository;

import com.transittracker.entity.GtfsStop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GtfsStopRepository extends CrudRepository<GtfsStop, String> {
}
