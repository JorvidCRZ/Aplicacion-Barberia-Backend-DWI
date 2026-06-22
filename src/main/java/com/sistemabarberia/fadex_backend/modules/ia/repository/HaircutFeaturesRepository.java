package com.sistemabarberia.fadex_backend.modules.ia.repository;

import com.sistemabarberia.fadex_backend.modules.ia.entity.HaircutFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HaircutFeaturesRepository extends JpaRepository<HaircutFeatures, Integer> {
}