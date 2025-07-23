package com.example.demo.mapper;

import com.example.demo.dto.request.CreateProviderRequest;
import com.example.demo.dto.request.UpdateProviderRequest;
import com.example.demo.dto.response.ProviderResponse;
import com.example.demo.entity.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    Provider toProvider(CreateProviderRequest request);
    ProviderResponse toProviderResponse(Provider provider);
    void updateProvider(@MappingTarget Integer providerId , UpdateProviderRequest request);
}
