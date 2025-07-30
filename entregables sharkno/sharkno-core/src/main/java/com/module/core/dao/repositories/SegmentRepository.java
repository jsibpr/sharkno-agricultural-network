package com.module.core.dao.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.SegmentMapper;
import com.module.core.models.Segment;

@Repository
public class SegmentRepository {

	@Autowired
	private JdbcTemplate template;

	//**********Geonames**********
	private static final String CITIES_TABLE = " geonames_cities CITY ";
	private static final String COUNTRIES_TABLE = " geonames_countries COUN ";

	//**********SEGMENTS**********
	private static final String SEGMENTS_TABLE = " segments SEG ";
	private static final String SEGMENT_FIELDS= "id, profileId, position, company, startDate, endDate, address, description, type";
	private static final String SEGMENT_UPDATE_FIELDS = "profileId = ?, position = ?, company = ?, startDate = ?, endDate = ?, address = ?, description = ?, type = ?";
	
	private static final String LEFT_JOIN = " left join ";


	public List<Segment> getAllSegment(String profileId) {
		return template.query("select SEG.*, CITY.asciiname, CITY.countryCode"
				             + " from " + SEGMENTS_TABLE 
				             + LEFT_JOIN + CITIES_TABLE    + "on SEG.address = CITY.id "
				             + LEFT_JOIN + COUNTRIES_TABLE + "on CITY.countryCode = COUN.ISO "
				             + "where SEG.profileId = ?", new Object[] {profileId}, new SegmentMapper());
	}

	public List<Segment> getSegment(String profileId, Segment.Type type) {
		return template.query("select SEG.*, CITY.asciiname, CITY.countryCode"
				             + " from " + SEGMENTS_TABLE
				             + LEFT_JOIN + CITIES_TABLE    + "on SEG.address = CITY.id "
				             + LEFT_JOIN + COUNTRIES_TABLE + "on CITY.countryCode = COUN.ISO " 
				             + " where SEG.profileId = ? and SEG.type = ?", new Object[] {profileId, type.toString()}, new SegmentMapper());
	}

	public void updateSegment(Segment segment, String profileId) {
		template.update("update " + SEGMENTS_TABLE + " set " + SEGMENT_UPDATE_FIELDS + " where id = ?", profileId, segment.getPosition(), segment.getCompany(),
				segment.getStartDate().toString(), segment.getEndDate() != null ? segment.getEndDate().toString() : null, segment.getAddress().getId().toString(),
						segment.getDescription(), segment.getType().toString(), segment.getId());
	}

	public void createSegment(Segment segment, String profileId) {
		template.update("insert into segments (" + SEGMENT_FIELDS + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?)", segment.getId(), profileId, segment.getPosition(),
				segment.getCompany(), segment.getStartDate().toString(), segment.getEndDate() != null ? segment.getEndDate().toString() : null,
						segment.getAddress().getId().toString(), segment.getDescription(), segment.getType().toString());
	}

	public void deleteSegment(String id) {
		template.update("delete from " + SEGMENTS_TABLE + " where id = ?", id);
	}

}
