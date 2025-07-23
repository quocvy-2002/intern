package com.example.demo.repository;

import com.example.demo.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByProviderName(String providerName);
    Optional<Provider> findByProviderId(Integer providerId);
    void deleteByProviderId(Integer providerId);
}
