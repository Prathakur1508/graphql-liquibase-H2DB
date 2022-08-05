package com.stream.stringopti.dao;

import com.stream.stringopti.entity.StreamOpti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<StreamOpti, Long> {
    Optional<StreamOpti> findById(Long id);
    List<StreamOpti> findByName(String name);
}
