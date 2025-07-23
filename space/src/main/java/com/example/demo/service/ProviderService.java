package com.example.demo.service;

import com.example.demo.dto.request.CreateProviderRequest;
import com.example.demo.dto.request.UpdateProviderRequest;
import com.example.demo.dto.response.ProviderResponse;
import com.example.demo.entity.Provider;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.ProviderMapper;
import com.example.demo.repository.ProviderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProviderService {
    ProviderMapper providerMapper;
    ProviderRepository providerRepository;

    public ProviderResponse createProvider(CreateProviderRequest request) {
        Provider provider = providerRepository.findByProviderName(request.getProviderName())
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        providerMapper.toProvider(request);
        providerRepository.save(provider);
        return providerMapper.toProviderResponse(provider);
    }

    public ProviderResponse updateProvider(Integer providerId ,UpdateProviderRequest request) {
        Provider provider = providerRepository.findByProviderId(providerId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        providerMapper.updateProvider(providerId, request);
        providerRepository.save(provider);
        return providerMapper.toProviderResponse(provider);
    }

    public void deleteProvider(Integer providerId) {
        providerRepository.deleteByProviderId(providerId);

    }

    public ProviderResponse getProvider(Integer providerId) {
        Provider provide = providerRepository.findByProviderId(providerId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        return providerMapper.toProviderResponse(provide);
    }

    public List<ProviderResponse> getProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providers.stream().map(providerMapper::toProviderResponse).collect(Collectors.toList());
    }
}
