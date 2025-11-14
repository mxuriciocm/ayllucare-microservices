package com.microservice.profiles.application.internal.queryservices;

import com.microservice.profiles.domain.model.aggregates.Profile;
import com.microservice.profiles.domain.model.queries.GetAllProfilesQuery;
import com.microservice.profiles.domain.model.queries.GetProfileByIdQuery;
import com.microservice.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.microservice.profiles.domain.services.ProfileQueryService;
import com.microservice.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {
    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query){
        return profileRepository.findById(query.userId());
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query){
        return profileRepository.findByUserId(query.userId());
    }

    @Override
    public List<Profile> handle(GetAllProfilesQuery query){
        return profileRepository.findAll();
    }
}
