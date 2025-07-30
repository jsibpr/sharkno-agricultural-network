package com.module.core.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.clients.NotificationClient;
import com.module.core.dao.repositories.PaymentRepository;
import com.module.core.dao.repositories.ProductRepository;
import com.module.core.dao.repositories.ProfileRepository;
import com.module.core.dao.repositories.ServiceRepository;
import com.module.core.exceptions.ForbiddenException;
import com.module.core.exceptions.NotFoundException;
import com.module.core.exceptions.SharknoException;
import com.module.core.models.Address;
import com.module.core.models.Entity;
import com.module.core.models.Notification;
import com.module.core.models.Session;
import com.module.core.models.product.Product;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.AppliedService;
import com.module.core.models.service.BasicService;
import com.module.core.models.service.BasicService.Status;
import com.module.core.models.service.Candidate;
import com.module.core.models.view.ExternalServiceForView;
import com.module.core.models.view.ServiceForView;
import com.module.core.models.view.ServiceSearchParams;

import io.micrometer.core.instrument.util.StringUtils;

@Service
@Transactional
public class ServiceService {
	
	@Autowired
	ServiceRepository repository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	NotificationClient notificationClient;
	
	@Autowired
	MailNotificationService mailNotificationService;
	
	@Value("${mail.send}")
	private boolean sendEmail;
	

	public String createService (ServiceForView serviceForView, String originId) {
		com.module.core.models.service.Service service = new com.module.core.models.service.Service();
		String id = UUID.randomUUID().toString().substring(0,18);
		service.setId(id);
		service.setTitle(serviceForView.getTitle());
		service.setDescription(serviceForView.getDescription());
		service.setCreationDate(new Date());
		service.setLastUpdate(new Date());
		service.setAddress(new Address(serviceForView.getAddressId(),null, null));
		service.setCandidateType(serviceForView.getCandidateType());
		service.setCategory(new Entity(serviceForView.getCategoryId(), null));
		service.setType(serviceForView.getType());
		service.setAttachment(serviceForView.getAttachment());
		service.setSkills(serviceForView.getSkillsIds().stream().map(s -> new Entity(s, null)).collect(Collectors.toList()));
		service.setExperienceLevel(serviceForView.getExperienceLevel());
		service.setVacancies(serviceForView.getVacancies());
		service.setPayment(serviceForView.getPayment());
		service.setDuration(serviceForView.getDuration());
		service.setDedication(serviceForView.getDedication());
		service.setStatus(BasicService.Status.OPEN);
		repository.createService(service, originId);
		paymentRepository.createPayment(service.getId(), service.getPayment());
		if (service.getSkills()!=null) {
			service.getSkills().forEach(e -> entityService.createEntityRelation(e.getId(), service.getId(), "SKILL"));
		}
		return id;
	}
	
	public String createExternalService (ExternalServiceForView externalService, Session session) throws ForbiddenException  {
		com.module.core.models.service.Service service = new com.module.core.models.service.Service();
		String id = UUID.randomUUID().toString().substring(0,18);
		
		service.setId(id);
		service.setTitle(externalService.getTitle());
		service.setDescription(externalService.getDescription());
		service.setCreationDate(new Date());
		service.setLastUpdate(new Date());
		service.setAddress(new Address(externalService.getAddressId(),null, null));
		
		//Candidate type is set up as dual by default in external services
		service.setCandidateType(LiteProfile.Type.DUAL);
		service.setCategory(new Entity(externalService.getCategoryId(), null));
		service.setType(externalService.getType());
		service.setAttachment(externalService.getAttachment());
		service.setSkills(externalService.getSkillsIds().stream().map(s -> new Entity(s, null)).collect(Collectors.toList()));
		service.setExperienceLevel(externalService.getExperienceLevel());
		
		//Number of vacancies is set up as 0 by default in external services
		service.setVacancies(0);
		service.setDuration(externalService.getDuration());
		service.setDedication(externalService.getDedication());
		
		//If business ID is informed, the status is EXTERNAL_PENDING (business has to accept it), otherwise is EXTERNAL_PENDING
		if(StringUtils.isNotBlank(externalService.getBusinessId())) {
			service.setStatus(BasicService.Status.EXTERNAL_PENDING);
			repository.createService(service, externalService.getBusinessId());
			notifyBusinessService(service, externalService.getBusinessId(), session);
			addCandidateToExternalService(session, id, Candidate.Status.ACCEPTED);
		} else {
			service.setStatus(BasicService.Status.EXTERNAL_UNASSIGNED);
			repository.createService(service, null);
			addCandidateToExternalService(session, id, Candidate.Status.EVALUATED);
		}
		
		if (service.getSkills()!=null) {
			service.getSkills().forEach(e -> entityService.createEntityRelation(e.getId(), service.getId(), "SKILL"));
		}
		return id;
	}
	
	private void notifyBusinessService (com.module.core.models.service.Service service, String businessId, Session session) {
		Notification notification = new Notification(UUID.randomUUID().toString().substring(0, 18),
				businessId, "Nuevo servicio externo pendiente de validación: " + service.getTitle(), new Date(),
				null, "EXTERNAL_SERVICE", service.getId());
		notificationClient.createNotification(notification);
	}
	
	private void addCandidateToExternalService (Session session, String serviceId, Candidate.Status status) {
		String profileId = session.getUser().getId();
		String candidateId = UUID.randomUUID().toString().substring(0, 18);
		LiteProfile profile = new LiteProfile();
		profile.setId(profileId);
		Candidate candidate = new Candidate(candidateId, profile, status, serviceId);
		repository.addCandidateToService(candidate);
	}
	
	public void updateService (com.module.core.models.service.Service service) {
		repository.updateService(service);
		paymentRepository.updatePayment(service.getId(), service.getPayment());
	}
	
	public AppliedService getService (String userId, String serviceId) throws NotFoundException {
		com.module.core.models.service.Service service  = repository.getService(serviceId);
		if (service == null) {
			throw new NotFoundException();
		}
		service.setSkills(entityService.getSkills(serviceId));
		if (service.getOrigin().getId()!= null && service.getOrigin().getId().equals(userId)) {
			// Candidate information only available if request user is the service creator
			service.setCandidates(getOrderedCandidates(service));
		}
		service.setProducts(getProductsFromService(serviceId));
		AppliedService appliedService = new AppliedService(service.getId(), service.getTitle(), service.getDescription(), service.getCreationDate(), service.getLastUpdate(), service.getAddress(),
				service.getCandidateType(), service.getAverageServiceReview(), service.getCategory(), service.getType(), service.getAttachment(), service.getSkills(), service.getExperienceLevel(),
				service.getVacancies(), service.getPayment(), service.getDuration(), service.getDedication(), service.getCandidates(), service.getStatus(), service.getOrigin(), service.getProducts(),
				false);
		appliedService.setHasAlreadyApplied(hasAlreadyApplied(userId, serviceId));
		
		return appliedService;
	}
	
	public List<Candidate> getOrderedCandidates (com.module.core.models.service.Service service) {
		//Get candidates full list
		List<Candidate> candidates = getCandidates(service.getId());
		
		//Assign a rating for each candidate and store it in a map
		HashMap<String, Integer> candidateRatingMap = getCandidateRatingMap(service, candidates);
		
		//Order the map from highest to lowest rating value
		LinkedHashMap<String, Integer> orderedCandidateRatingMap = candidateRatingMap.entrySet()
		        .stream()
		        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		
		//Find the profiles of the map in the candidates list and store them in an ordered list
		List<Candidate> orderedCandidates = new ArrayList<>();
		for (String profileId : orderedCandidateRatingMap.keySet()) {
			orderedCandidates.add(candidates.stream().filter(c -> c.getProfile().getId().equals(profileId)).findAny().orElse(null));
		}
		return orderedCandidates;
	}

	private HashMap<String, Integer> getCandidateRatingMap(com.module.core.models.service.Service service, List<Candidate> candidates) {
		HashMap<String, Integer> candidateRatingMap = new HashMap<>();
		if (candidates != null && !candidates.isEmpty()) {
			for (Candidate candidate : candidates) {
				int rating = getCandidateRating(service, candidate);
				candidateRatingMap.put(candidate.getProfile().getId(), rating);
			}
		}
		return candidateRatingMap;
	}

	private int getCandidateRating(com.module.core.models.service.Service service, Candidate candidate) {
		int rating = 0;
		if (service.getSkills() != null && !service.getSkills().isEmpty()) { //Skip if the service has no skill
			List<Entity> candidateSkills = entityService.getSkills(candidate.getProfile().getId());
			if (candidateSkills != null && !candidateSkills.isEmpty()) {
				//For each skill in the service
				for (Entity serviceSkill : service.getSkills()) {
					//Increase the rating if the candidate has the skill required by the service
					if (candidateSkills.stream().map(Entity::getId).anyMatch(s -> s.equals(serviceSkill.getId()))){
						rating++;
					}
				}
			}
		}
		Integer candidateAddress = profileRepository.getAddress(candidate.getProfile().getId());
		//Increase rating by one if the address of the service is the same as the candidate
		if (candidateAddress != null && service.getAddress() != null && candidateAddress.equals(service.getAddress().getId())) {
			rating++;
		}
		return rating;
	}
	
	public List<Candidate> getCandidates (String serviceId) {
		return repository.getCandidates(serviceId);
	}
	
	public void addCandidateToService(Session session, String serviceId) throws ForbiddenException {
		String profileId = session.getUser().getId();
		com.module.core.models.service.Service service = repository.getService(serviceId);
		if (!hasAlreadyApplied(profileId, serviceId) && !profileId.equals(service.getOrigin().getId())) {
			String candidateId = UUID.randomUUID().toString().substring(0, 18);
			LiteProfile profile = new LiteProfile();
			profile.setId(profileId);
			Candidate candidate = new Candidate(candidateId, profile, Candidate.Status.PENDING, serviceId);
			repository.addCandidateToService(candidate);
			Notification notification = new Notification(UUID.randomUUID().toString().substring(0, 18),
					service.getOrigin().getId(), "Nuevo candidato para el servicio: " + service.getTitle(), new Date(),
					null, "SERVICE", service.getId());
			notificationClient.createNotification(notification);
			if (sendEmail) {
				mailNotificationService.notificationMail(notification);
			}
			sendNotificationToCandidate(candidateId, session, Candidate.Status.PENDING);
		} else {
			throw new ForbiddenException();
		}
	}
	
	public boolean hasAlreadyApplied(String profileId, String serviceId) {
		return repository.hasAlreadyApplied(profileId, serviceId);
	}

	public void updateCandidateStatus (String candidateId, Candidate.Status status, Session session) {
		Candidate candidate = new Candidate(candidateId, null, status, null);
		repository.updateCandidateStatus(candidate);
		sendNotificationToCandidate(candidateId, session, status);
	}
	
	public void sendNotificationToCandidate(String candidateId, Session session, Candidate.Status status) {
		String statusMessage= "";
		switch (status) {
		case PENDING:
			statusMessage= "Candidatura presentada.";
			break;
		case ACCEPTED:
			statusMessage= "Candidatura aceptada.";
			break;
		case REJECTED:
			statusMessage= "Candidatura rechazada.";
			break;
		case EVALUATED:
			statusMessage= "Servicio completado y trabajo evaluado.";
			break;
		}
		com.module.core.models.service.Service service = repository.getService(repository.getServiceIdFromCandidate(candidateId));
		String profileId = repository.getProfileIdFromCandidate(candidateId);
		String message = "Tu estado en el servicio: '"+ service.getTitle() + "' ha sido actualizado a: " + statusMessage;
		Notification notification = new Notification(UUID.randomUUID().toString().substring(0,18), profileId, message , new Date(), null, "SERVICE", service.getId());
		notificationClient.createNotification(notification);
		if (sendEmail) {
			mailNotificationService.notificationMail(notification);
		}
	}
	
	public List<BasicService> searchServicesByStatus (String candidateId, String titleFragment, Status status) {
		return repository.searchServicesByStatus(candidateId, titleFragment, status);
	}
	
	public List<BasicService> getMyServices (String originId) {
		return repository.getCompletedServices(originId);
	}
	
	public void updateServiceStatus (String serviceId, Status status, String userId) throws SharknoException{
		com.module.core.models.service.Service service = repository.getService(serviceId);
		if (service == null) {
			//Service not found
			throw new NotFoundException();
		} else if (!userId.equals(service.getOrigin().getId())) {
			//Users may only modify services they own
			throw new ForbiddenException();
		} else {
			repository.updateServiceStatus(serviceId, status);			
		}
		repository.updateServiceStatus(serviceId, status);
	}
	
	public List<BasicService> getCompletedServices (String profileId) {
		return repository.getCompletedServices(profileId);
	}
	
	public List<BasicService> getActiveServices (String profileId) {
		return repository.getActiveServices(profileId);
	}

	public List<BasicService> getByStatusServices(Status status, String profileId) {
		return repository.getByStatusServices(status, profileId);
	}

	public List<BasicService> searchServices(ServiceSearchParams params, String idUser) {
		// Profile type filter only used for search not including origin ID
		LiteProfile.Type profileType = params.getOriginId() == null ? profileRepository.getLiteProfile(idUser).getType() : null;
		return repository.searchServices(params, profileType);
	}
	
	public void addProductToService(String productId, String serviceId) {
		repository.addProductToService(productId, serviceId);
	}
	
	public void deleteProductFromService(String productId, String serviceId) {
		repository.deleteProductFromService(productId, serviceId);
	}
	
	public List<Product> getProductsFromService(String serviceId){
		List<String>productsIds = productRepository.getProductsfromService(serviceId);
		List<Product>products = new ArrayList<>();
		productsIds.forEach((String productId) -> products.add(productRepository.getProduct(productId)));
		return products;
	}
		
}