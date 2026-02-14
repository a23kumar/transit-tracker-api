package com.transittracker.repository;

import com.transittracker.entity.GtfsRoute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GtfsRouteRepository extends CrudRepository<GtfsRoute, String> {
}
