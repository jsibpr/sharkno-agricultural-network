package com.module.core.dao.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.BasicServiceMapper;
import com.module.core.dao.mappers.CandidateMapper;
import com.module.core.dao.mappers.ServiceMapper;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService;
import com.module.core.models.service.BasicService.Status;
import com.module.core.models.service.Candidate;
import com.module.core.models.service.Service;
import com.module.core.models.view.ServiceSearchParams;

@Repository
public class ServiceRepository {

	@Autowired
	private JdbcTemplate template;

	private static final String SERVICES_TABLE = " services SER ";
	private static final String PROFILES_TABLE = " profiles PRO ";
	private static final String ABOUT_ME_TABLE = " aboutme ABM ";
	private static final String ENTITIES_TABLE = " entities ENT ";
	private static final String SERVICES_CANDIDATES_TABLE  = " services_candidates SERCAN ";
	private static final String SERVICES_PRODUCTS_TABLE = " services_products SERPROD ";
	private static final String ENTITIES_RELATIONS_TABLE = " entities_relations ENTREL ";
	private static final String SERVICE_FIELDS = "id, title, description, creationDate, lastUpdate, address, category, type, attachment, experienceLevel, vacancies, duration, dedication, status, origin, candidateType";
	private static final String SERVICE_UPDATE_FIELDS = "title = ?, description = ?, creationDate = ?, lastUpdate = ?, address = ?, category = ?, type = ?, attachment = ?, experienceLevel = ?, vacancies = ?, duration = ?, dedication = ?, status = ?, origin = ?, candidateType = ?";
	private static final String SERVICES_CANDIDATES_STATE_UPDATE_FIELDS = " status = ? ";
	private static final String SERVICES_STATE_UPDATE_FIELDS = " status = ? ";
	private static final String SERVICES_CANDIDATES_FIELDS = "id,serviceId,profileId,status";
	private static final String SERVICES_PRODUCTS_TABLE_FIELDS = "productId, serviceId";
	private static final String SERVICES_PRODUCTS_DELETE_FIELDS = "productId = ? AND  serviceId = ?";
	private static final String FROM = " from ";
	private static final String JOIN = " join ";
	private static final String LEFT_JOIN = " left join ";
	private static final String UPDATE = "update";
	private static final String LEFT_JOIN_SERVICES_CITIES = " left join geonames_cities CITY on SER.address = CITY.id ";
	private static final String LEFT_JOIN_SERVICES_PAYMENTS = " left join payments PAY on SER.id = PAY.id ";
	private static final String WHERE_ID = "where id = ?";
	
	public void createService(Service service, String originId) {
		template.update(
				"INSERT INTO services (" + SERVICE_FIELDS
						+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				service.getId(), service.getTitle(), service.getDescription(), service.getCreationDate(),
				service.getLastUpdate(), service.getAddress().getId(), service.getCategory().getId(),
				service.getType().toString(), service.getAttachment(), service.getExperienceLevel().toString(),
				service.getVacancies(), service.getDuration().toString(), service.getDedication().toString(),
				service.getStatus().toString(), originId, service.getCandidateType().toString());
	}

	public void updateService(Service service) {
		template.update(UPDATE + SERVICES_TABLE + "set " + SERVICE_UPDATE_FIELDS + WHERE_ID, service.getTitle(),
				service.getDescription(), service.getCreationDate(), service.getLastUpdate(),
				service.getAddress().getId(), service.getCategory().getId(), service.getType().toString(),
				service.getAttachment(), service.getExperienceLevel().toString(), service.getVacancies(),
				service.getDuration().toString(), service.getDedication().toString(), service.getStatus().toString(),
				service.getOrigin().getId(), service.getCandidateType().toString(), service.getId());
	}

	public Service getService(String id) {
		try {
			return template.queryForObject(
					"select SER.*, ENT.id, ENT.name as categoryName, CITY.asciiname, CITY.countryCode, PAY.type as paymentType, PAY.minAmount, PAY.maxAmount, PAY.currency, "
							+ "PRO.id, PRO.name, PRO.birthDate, PRO.email, PRO.phone, PRO.address, PRO.profilePicture, PRO.type as profileType, PRO.salary, PRO.averageReview, PRO.likeQty,"
							+ "ABM.id as aboutMeid, ABM.profileId, ABM.shortDescription, ABM.longDescription" 
							+ FROM + SERVICES_TABLE 
							+ LEFT_JOIN + PROFILES_TABLE + "on SER.origin = PRO.id " 
							+ LEFT_JOIN + ABOUT_ME_TABLE + "on SER.origin = ABM.profileId" 
							+ LEFT_JOIN + ENTITIES_TABLE + "on SER.category = ENT.id and ENT.type = 'CATEGORY'" 
							+ LEFT_JOIN_SERVICES_CITIES 
							+ LEFT_JOIN_SERVICES_PAYMENTS 
							+ " where SER.id = ?", new Object[] { id }, new ServiceMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public String getServiceIdFromCandidate (String candidateId) {
		return template.queryForObject(
				"SELECT SERCAN.serviceId" + FROM + SERVICES_CANDIDATES_TABLE + "WHERE SERCAN.id = ?",
				new Object[] { candidateId }, String.class);
	}
	
	public String getProfileIdFromCandidate (String candidateId) {
		return template.queryForObject(
				"SELECT SERCAN.profileId" + FROM + SERVICES_CANDIDATES_TABLE + "WHERE SERCAN.id = ?",
				new Object[] { candidateId }, String.class);
	}

	public void addCandidateToService(Candidate candidate) {
		template.update(
				"INSERT INTO services_candidates (" + SERVICES_CANDIDATES_FIELDS + ") values (?, ?, ?, ?)",
				candidate.getId(), candidate.getServiceId(), candidate.getProfile().getId(), candidate.getStatus().toString());
	}
	
	public boolean hasAlreadyApplied(String profileId, String serviceId) {
		return (template.queryForObject(
				"SELECT count(profileId)" + FROM + SERVICES_CANDIDATES_TABLE 
				+ "WHERE SERCAN.profileId = ? AND SERCAN.serviceId = ?", new Object[] { profileId, serviceId }, Integer.class)) > 0;
	}

	public void updateCandidateStatus(Candidate candidate) {
		template.update(
				UPDATE + SERVICES_CANDIDATES_TABLE + "set " + SERVICES_CANDIDATES_STATE_UPDATE_FIELDS 
				+ WHERE_ID, candidate.getStatus().toString(), candidate.getId());
	}

	public List<BasicService> searchServicesByStatus(String candidateId, String titleFragment, Status status) {
		String likeParameter = "%" + titleFragment + "%";
		String statement = "SELECT DISTINCT SER.*, CITY.asciiname, CITY.countryCode"
				.concat(FROM + SERVICES_TABLE)
				.concat(LEFT_JOIN_SERVICES_CITIES)
				.concat(" WHERE SER.status = ?").concat(" AND SER.title LIKE ?").concat(" AND SER.id NOT IN (")
				.concat(" SELECT SERCAN.serviceId" +  FROM + SERVICES_CANDIDATES_TABLE + "WHERE SERCAN.profileId = ?)");
		List<Object> parameterList = new ArrayList<>();
		parameterList.add(status.toString());
		parameterList.add(likeParameter);
		parameterList.add(candidateId);
		return template.query(statement, parameterList.toArray(), new BasicServiceMapper());
	}

	public List<BasicService> searchServices(ServiceSearchParams params, LiteProfile.Type profileType) {
		String likeParameter = params.getFragment() != null ? "%" + params.getFragment() + "%" : null;
		String statement = "SELECT SER.*, CITY.asciiname, CITY.countryCode".concat(FROM + SERVICES_TABLE)
				.concat(LEFT_JOIN_SERVICES_CITIES)
				.concat(LEFT_JOIN_SERVICES_PAYMENTS)
				.concat(" WHERE 1 = 1 ").concat(params.getFragment() != null ? " AND SER.title LIKE ?" : "")
				.concat(params.getAddressId() != null ? " AND SER.address = ?" : "")
				.concat(params.getCategoryId() != null ? " AND SER.category = ?" : "")
				.concat(params.getOriginId() != null ? " AND SER.origin = ?" : "")
				.concat(params.getNotOriginId() != null ? " AND SER.origin != ?" : "")
				.concat(params.getCreationDateFrom() != null ? " AND SER.creationDate > ?" : "")
				.concat(params.getCreationDateTo() != null ? " AND SER.creationDate < ?" : "")
				.concat(params.getDedication() != null ? " AND SER.dedication = ?" : "")
				.concat(params.getDuration() != null ? " AND SER.duration = ?" : "")
				.concat(params.getExperienceLevel() != null ? " AND SER.experienceLevel = ?" : "")
				.concat(params.getType() != null ? " AND SER.type = ?" : "")
				.concat(params.getStatus() != null ? " AND SER.status = ?" : "")
				.concat(params.getVacancies() != null ? " AND SER.vacancies >= ?" : "")
				.concat(profileType != null && !profileType.equals(LiteProfile.Type.DUAL) ? " AND SER.candidateType in (?,?)" : "")
				.concat(params.getSkillId() != null ? " AND SER.id in (SELECT id"+ FROM + ENTITIES_RELATIONS_TABLE  + "WHERE entityId = ? AND entityUse = 'SKILL') " : "")
				.concat(params.getCandidateId() != null ? " AND SER.id in (SELECT serviceId" + FROM + SERVICES_CANDIDATES_TABLE + "WHERE profileId = ?) AND SER.status IN (?, ?)" : "")
				.concat(params.getNotCandidateId() != null ? " AND SER.id not in (SELECT serviceId" + FROM + SERVICES_CANDIDATES_TABLE + "WHERE profileId = ?)" : "")
				.concat(params.getPaymentType() != null ? " AND PAY.type = ?" : "")
				.concat(params.getLimit() != null ? " LIMIT ?" : "");
		List<Object> parameterList = new ArrayList<>();
		addToListIfNotNull(parameterList, likeParameter);
		addToListIfNotNull(parameterList, params.getAddressId());
		addToListIfNotNull(parameterList, params.getCategoryId());
		addToListIfNotNull(parameterList, params.getOriginId());
		addToListIfNotNull(parameterList, params.getNotOriginId());
		addToListIfNotNull(parameterList, params.getCreationDateFrom());
		addToListIfNotNull(parameterList, params.getCreationDateTo());
		addToListIfNotNull(parameterList, params.getDedication() != null ? params.getDedication().toString() : null);
		addToListIfNotNull(parameterList, params.getDuration() != null ? params.getDuration().toString() : null);
		addToListIfNotNull(parameterList, params.getExperienceLevel() != null ? params.getExperienceLevel().toString() : null);
		addToListIfNotNull(parameterList, params.getType() != null ? params.getType().toString() : null);
		addToListIfNotNull(parameterList, params.getStatus() != null ? params.getStatus().toString() : null);
		addToListIfNotNull(parameterList, params.getVacancies());
		addToListIfNotNull(parameterList, profileType != null && !profileType.equals(LiteProfile.Type.DUAL) ? LiteProfile.Type.DUAL.toString() : null);
		addToListIfNotNull(parameterList, profileType != null && !profileType.equals(LiteProfile.Type.DUAL) ? profileType.toString() : null);
		addToListIfNotNull(parameterList, params.getSkillId());
		addToListIfNotNull(parameterList, params.getCandidateId());
		if (params.getCandidateId() != null && !params.getCandidateId().isEmpty()) {
			parameterList.add(Status.OPEN.toString());
			parameterList.add(Status.IN_PROGRESS.toString());
		}
		addToListIfNotNull(parameterList, params.getNotCandidateId());
		addToListIfNotNull(parameterList, params.getPaymentType() != null ? params.getPaymentType().toString() : null);
		addToListIfNotNull(parameterList, params.getLimit());
		return template.query(statement, parameterList.toArray(), new BasicServiceMapper());
	}

	public void updateServiceStatus(String serviceId, Status status) {
		template.update(UPDATE + SERVICES_TABLE + "set " + SERVICES_STATE_UPDATE_FIELDS + WHERE_ID, status.toString(), serviceId);
	}

	public List<Candidate> getCandidates(String serviceId) {
		return template.query(
				"select SERCAN.id, SERCAN.serviceId, SERCAN.profileId, SERCAN.status, PRO.name, PRO.profilePicture, PRO.type, PRO.salary, PRO.email, PRO.averageReview, PRO.likeQty,"
				+ " ABM.id, ABM.shortDescription, ABM.longDescription"
						+ FROM + SERVICES_CANDIDATES_TABLE
						+ LEFT_JOIN + PROFILES_TABLE + "on PRO.id = SERCAN.profileId" 
						+ LEFT_JOIN + ABOUT_ME_TABLE + "on PRO.id = ABM.profileId " 
						+ "where SERCAN.serviceId = ?",new Object[] { serviceId }, new CandidateMapper());
	}

	public List<BasicService> getCompletedServices(String profileId) {
		return template.query(
				"select SER.*, asciiname, countryCode" 
		        + FROM + SERVICES_TABLE 
				+ LEFT_JOIN	+ SERVICES_CANDIDATES_TABLE + "on SER.id = SERCAN.serviceId" 
	            + LEFT_JOIN_SERVICES_CITIES
				+ " where SERCAN.profileId = ? and SERCAN.status IN (?, ?) and SER.STATUS IN (?, ?, ?, ?, ?)",
				new Object[] { profileId, Candidate.Status.ACCEPTED.toString(), Candidate.Status.EVALUATED.toString(), Status.EXTERNAL_PENDING.toString(), Status.EXTERNAL_COMPLETED.toString(), Status.EXTERNAL_UNASSIGNED.toString(), Status.EXTERNAL_REJECTED.toString(), Status.COMPLETED.toString() }, new BasicServiceMapper());
	}

	public List<BasicService> getActiveServices(String profileId) {
		return template.query(
				"select SER.*, CITY.asciiname, CITY.countryCode" 
		                + FROM + SERVICES_CANDIDATES_TABLE
						+ JOIN + SERVICES_TABLE + "on SER.id = SERCAN.serviceId" 
						+ LEFT_JOIN_SERVICES_CITIES 
						+ " where SERCAN.profileId = ? and SERCAN.status in ( ?, ?)", 
						new Object[] { profileId, Candidate.Status.ACCEPTED.toString(), Candidate.Status.PENDING.toString() }, new BasicServiceMapper());
	}

	public List<BasicService> getByStatusServices(Status status, String profileId) {
		return template.query(
				"select SER.*, asciiname, countryCode" 
						+ FROM + SERVICES_TABLE
						+ LEFT_JOIN_SERVICES_CITIES 
						+ " where SER.origin = ? and SER.STATUS = ?", new Object[] { profileId, status.toString() }, new BasicServiceMapper());
	}
	
	public void addProductToService(String productId, String serviceId) {
		template.update("INSERT INTO services_products (" + SERVICES_PRODUCTS_TABLE_FIELDS + ")" + " values (?, ?)", productId, serviceId);
	}
	
	public void deleteProductFromService(String productId, String serviceId) {
		template.update("DELETE" + FROM + SERVICES_PRODUCTS_TABLE + "WHERE (" + SERVICES_PRODUCTS_DELETE_FIELDS + ")", productId, serviceId );
	}
	
	public List<String> getServicesFromProduct(String productId){
		List<String> servicesIds = new ArrayList<>();  
		
		return template.query(
				"SELECT serviceId" + FROM + SERVICES_PRODUCTS_TABLE 
				+ "WHERE productId = ?", new Object[] { productId }, new ResultSetExtractor<List<String>>() {
			@Override
			 public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				  while(rs.next()) {
					  servicesIds.add(rs.getString("serviceId"));
				  }
				  return servicesIds;
			}
		});
	}
	
	private void addToListIfNotNull(List<Object> list, Object object) {
		if (object != null) {
			list.add(object);
		}
	}
}
