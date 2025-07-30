package com.module.core.dao.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.BasicProfileMapper;
import com.module.core.dao.mappers.LiteProfileMapper;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.BasicProfile; 
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.view.ProfileSearchParams;

@Repository
public class ProfileRepository {

		@Autowired
		private JdbcTemplate template;
		
		//**********Geonames**********
		private static final String CITIES_TABLE = " geonames_cities CITY ";
		private static final String COUNTRIES_TABLE = " geonames_countries COUN ";

		//**********Profiles**********
		private static final String PROFILES_TABLE = " profiles PRO ";
		private static final String PROFILE_FIELDS= "id, name, birthDate, email, phone, address, profilePicture, type, creationDate, lastUpdate, salary, web, averageReview";
		private static final String PROFILE_UPDATE_FIELDS = "name = ?, birthDate = ?, email = ?, phone = ?, address = ?, profilePicture = ?, type = ?, lastUpdate = ?, salary = ?, web = ?";
		private static final String ABOUT_ME_TABLE = " aboutme ABM ";
		private static final String ABOUT_ME_FIELDS = "id, shortDescription , longDescription, profileId";
		private static final String ABOUT_ME_UPDATE_FIELDS = "shortDescription = ?, longDescription = ?";
		private static final String JOIN_FIELDS = "PRO.id pro_id, name, likeQty, ABM.id abm_id, shortDescription, longDescription, profilePicture, email, type, salary, averageReview";
		
		//*********Contacts***********
		private static final String CONTACTS_TABLE = " contacts CONT ";
		private static final String CONTACTS_TABLE2 = " contacts CONT2 ";
		
		private static final String WHERE_ID = " where id = ?";
		private static final String FROM = " FROM ";
		private static final String LEFT_JOIN = " left join ";
		private static final String JOIN = " join ";

		public BasicProfile getBasicProfile(String id) {
			try {
				return template.queryForObject("select PRO.*, CITY.asciiname, CITY.countryCode, ABM.id, ABM.shortDescription, ABM.longDescription"
											+ FROM + PROFILES_TABLE
											+ LEFT_JOIN + CITIES_TABLE    + "on PRO.address = CITY.id"
											+ LEFT_JOIN + COUNTRIES_TABLE + "on CITY.countryCode = COUN.ISO" 
											+ LEFT_JOIN + ABOUT_ME_TABLE   + "on PRO.id = ABM.profileId"
											+ " where PRO.id = ?"
											, new Object[] {id}, new BasicProfileMapper());
			} catch (EmptyResultDataAccessException e) {
				// Empty result in database
				return null;
			}
		}

		public void createBasicProfile(BasicProfile basicprofile) {
			template.update("insert into profiles (" + PROFILE_FIELDS + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", basicprofile.getId(), basicprofile.getName(),
					basicprofile.getBirthDate(), basicprofile.getEmail(), basicprofile.getPhone(), basicprofile.getAddress().getId(), basicprofile.getProfilePicture(), basicprofile.getType().toString(),
					new Date(), new Date(), basicprofile.getSalary(), basicprofile.getWeb(), null);
		}

		public int updateBasicProfile(BasicProfile basicprofile) {
			return template.update("update " + PROFILES_TABLE + " set " + PROFILE_UPDATE_FIELDS + WHERE_ID, basicprofile.getName(), basicprofile.getBirthDate(), basicprofile.getEmail(),
					basicprofile.getPhone(), basicprofile.getAddress().getId(), basicprofile.getProfilePicture(), basicprofile.getType().toString(), new Date(), basicprofile.getSalary(),
					basicprofile.getWeb(), basicprofile.getId());
		}
		
		public void createAboutMe(AboutMe aboutMe, String profileId) {
			template.update("insert into aboutme (" + ABOUT_ME_FIELDS + ") values (?, ?, ?, ?)", aboutMe.getId(), aboutMe.getShortDescription(), aboutMe.getLongDescription(), profileId);
		}
		
		//profileId parameter is not used
		public void updateAboutMe(AboutMe aboutMe, String profileId) {
			template.update("UPDATE " + ABOUT_ME_TABLE + " set " + ABOUT_ME_UPDATE_FIELDS + WHERE_ID, aboutMe.getShortDescription(), aboutMe.getLongDescription(), aboutMe.getId());
		}

		public List<LiteProfile> getContacts(String fragment, int limit, String id) {
			String likeParameter ="%" + fragment + "%";
			return template.query("select distinct " + JOIN_FIELDS + FROM + PROFILES_TABLE + JOIN + ABOUT_ME_TABLE + "on ABM.profileId = PRO.id where (PRO.id in " +
					"(select origin from " + CONTACTS_TABLE + "where destination = ? AND CONT.status = 'CONFIRMED') or PRO.id in (select destination from " + CONTACTS_TABLE2 +
					"where origin = ? AND CONT2.status = 'CONFIRMED')) AND name LIKE ? limit ?",	new Object[] {id,id, likeParameter, limit}, new LiteProfileMapper());
		}

		public List<LiteProfile> searchProfiles(ProfileSearchParams params, String idUser) {
			String likeParameter = params.getFragment() != null ? "%" + params.getFragment() + "%" : null;
			String statement = "SELECT distinct " + JOIN_FIELDS + FROM + PROFILES_TABLE + JOIN + ABOUT_ME_TABLE + "on ABM.profileId = PRO.id where 1 = 1 "
					.concat(idUser != null ? " AND PRO.id != ?" : "")
					.concat(params.getIsContact() != null && params.getIsContact() ? " AND (PRO.id in (select origin from " + CONTACTS_TABLE +
							" where destination = ?) or PRO.id in (select destination from " + CONTACTS_TABLE + " where origin = ?))" : "")
					.concat(params.getIsContact() != null && !params.getIsContact() ? " AND PRO.id not in (select origin from " + CONTACTS_TABLE + 
							" where destination = ?) and PRO.id not in (select destination from " + CONTACTS_TABLE + " where origin = ?)" : "")
					.concat(params.getFragment() != null ? " AND name LIKE ?" : "")
					.concat(params.getAddressId() != null ? " AND address = ? " : "")
					.concat(params.getSalaryFrom() != null ? " AND salary >= ? " : "")
					.concat(params.getSalaryTo() != null ? " AND salary <= ? " : "")
					.concat(params.getType() != null ? " AND type = ? " : "")
					.concat(params.getSkillId() != null ? " AND PRO.id in (SELECT id FROM entities_relations WHERE entityId = ? AND entityUse = 'SKILL')" : "")
					.concat(params.getLimit() != null ? " LIMIT ? " : "");
			List<Object> parameterList = new ArrayList<>();
			addToListIfNotNull(parameterList, idUser);
			if (params.getIsContact() != null) {
				parameterList.add(idUser);
				parameterList.add(idUser);
			}
			addToListIfNotNull(parameterList, likeParameter);
			addToListIfNotNull(parameterList, params.getAddressId());
			addToListIfNotNull(parameterList, params.getSalaryFrom());
			addToListIfNotNull(parameterList, params.getSalaryTo());
			addToListIfNotNull(parameterList, params.getType() != null ? params.getType().toString() : null);
			addToListIfNotNull(parameterList, params.getSkillId());
			addToListIfNotNull(parameterList, params.getLimit());
			return template.query(statement, parameterList.toArray(), new LiteProfileMapper());
		}
		
		public LiteProfile getLiteProfile(String id) {
			try {
				return template.queryForObject("SELECT " + JOIN_FIELDS + FROM + PROFILES_TABLE + JOIN + ABOUT_ME_TABLE + "on ABM.profileId = PRO.id where PRO.id = ?",
						new Object[] {id}, new LiteProfileMapper());
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}

		private void addToListIfNotNull (List<Object> list, Object object) {
			if (object != null) {
				list.add(object);
			}
		}

		public String getEmail(String id) {
			try {
				return template.queryForObject("SELECT email from " + PROFILES_TABLE + WHERE_ID, new Object[]{id} , String.class);
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}
		
		public Integer getAddress(String id) {
			try {
				return template.queryForObject("SELECT address from " + PROFILES_TABLE + WHERE_ID, new Object[]{id} , Integer.class);
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}
		
		public void updateProfileImage (String imageUrl, String profileId) {
			template.update("UPDATE" + PROFILES_TABLE + " SET  profilePicture = ? WHERE id = ?", imageUrl, profileId);
		}

}
