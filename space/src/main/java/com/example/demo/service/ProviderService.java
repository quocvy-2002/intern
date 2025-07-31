package com.example.demo.service;

import com.example.demo.model.dto.provider.ProviderCreateDTO;
import com.example.demo.model.dto.provider.ProviderUpdateDTO;
import com.example.demo.model.dto.provider.ProviderDTO;
import com.example.demo.model.entity.Provider;
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

    public ProviderDTO createProvider(ProviderCreateDTO request) {
        Provider provider = providerRepository.findByProviderName(request.getProviderName())
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        providerMapper.toProvider(request);
        providerRepository.save(provider);
        return providerMapper.toProviderResponse(provider);
    }

    public ProviderDTO updateProvider(Integer providerId , ProviderUpdateDTO request) {
        Provider provider = providerRepository.findByProviderId(providerId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        providerMapper.updateProvider(provider, request);
        providerRepository.save(provider);
        return providerMapper.toProviderResponse(provider);
    }

    public void deleteProvider(Integer providerId) {
        providerRepository.deleteByProviderId(providerId);

    }

    public ProviderDTO getProvider(Integer providerId) {
        Provider provide = providerRepository.findByProviderId(providerId)
                .orElseThrow(() -> new AppException(ErrorCode.SPACE_TYPE_NOT_FOUND));
        return providerMapper.toProviderResponse(provide);
    }

    public List<ProviderDTO> getProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providers.stream().map(providerMapper::toProviderResponse).collect(Collectors.toList());
    }
}
