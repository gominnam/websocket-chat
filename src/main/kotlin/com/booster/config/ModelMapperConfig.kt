package com.booster.config

import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {
    @Bean
    fun modelMapper(): ModelMapper {
        var modelMapper = ModelMapper()
        modelMapper.configuration.setMatchingStrategy(MatchingStrategies.STRICT)
        return modelMapper
    }

}