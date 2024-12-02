package com.github.sats17.mockserver.repository;

import com.github.sats17.mockserver.model.h2.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, String> {
}
