package com.example.demo.mapper;

import com.example.demo.dto.request.CreateProviderRequest;
import com.example.demo.dto.request.UpdateProviderRequest;
import com.example.demo.dto.response.ProviderResponse;
import com.example.demo.entity.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    @Mapping(target = "providerName", source = "providerName")
    Provider toProvider(CreateProviderRequest request);

    ProviderResponse toProviderResponse(Provider provider);

    @Mapping(target = "providerName", source = "providerName")
    void updateProvider(@MappingTarget Provider provider, UpdateProviderRequest request);
}
