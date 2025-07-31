package com.example.demo.mapper;

import com.example.demo.model.dto.provider.ProviderCreateDTO;
import com.example.demo.model.dto.provider.ProviderUpdateDTO;
import com.example.demo.model.dto.provider.ProviderDTO;
import com.example.demo.model.entity.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    @Mapping(target = "providerName", source = "providerName")
    Provider toProvider(ProviderCreateDTO request);

    ProviderDTO toProviderResponse(Provider provider);

    @Mapping(target = "providerName", source = "providerName")
    void updateProvider(@MappingTarget Provider provider, ProviderUpdateDTO request);
}
