package com.module.core.dao.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.AddressMapper;
import com.module.core.dao.mappers.CountryMapper;
import com.module.core.models.Address;
import com.module.core.models.Country;

@Repository
public class LocationRepository {

	@Autowired
	JdbcTemplate template;
	
	private static final String COUNTRIES_TABLE = "geonames_countries";
	private static final String CITIES_TABLE = "geonames_cities";
	
	public List<Country> suggestCountries (String fragment) {
		String likeParameter = fragment + "%";
		return template.query("SELECT ISO, name FROM " + COUNTRIES_TABLE + " WHERE name LIKE ?",new Object[] {likeParameter}, new CountryMapper());
	}
	
	public List<Address> suggestCitiesInCountry (String country, String fragment) {
		String likeParameter = fragment + "%";
		return template.query("SELECT id, name, countryCode, population FROM " + CITIES_TABLE + " WHERE countryCode = ? AND name LIKE ? ORDER BY population DESC",new Object[] {country,likeParameter}, new AddressMapper());
	}
	
	public List<Address> suggestCities (String fragment) {
		String likeParameter = fragment + "%";
		return template.query("SELECT id, name, countryCode, population FROM " + CITIES_TABLE + " WHERE name LIKE ? ORDER BY population DESC",new Object[] {likeParameter}, new AddressMapper());
	}
	
}
