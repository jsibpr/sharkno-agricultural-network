package com.module.core.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.module.core.dao.repositories.EntityRepository;
import com.module.core.dao.repositories.LocationRepository;
import com.module.core.models.Address;
import com.module.core.models.Country;
import com.module.core.models.Entity;

@Service
public class SuggestService {
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	EntityRepository entityRepository;
	
	public List<Country> suggestCountries (String fragment) {
		return locationRepository.suggestCountries(fragment);
	}
	
	public List<Address> suggestCitiesInCountry (String country, String fragment) {
		return StringUtils.isEmpty(country) ? locationRepository.suggestCities(fragment) : locationRepository.suggestCitiesInCountry(country, fragment);
	}

	public List<Address> suggestCities (String fragment) {
		return locationRepository.suggestCities(fragment);
	}
	
	public List<Entity> suggestEntities (String type, String fragment) {
		return entityRepository.suggestEntity(type, fragment);
	}

}
