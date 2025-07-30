package com.module.core.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.dao.repositories.ProfileRepository;
import com.module.core.dao.repositories.SegmentRepository;
import com.module.core.exceptions.NotFoundException;
import com.module.core.models.Address;
import com.module.core.models.Entity;
import com.module.core.models.Segment;
import com.module.core.models.Recommendation;
import com.module.core.models.Review;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.BasicProfile;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.profile.LiteProfile.Type;
import com.module.core.models.profile.Profile;
import com.module.core.models.profile.PublicProfile;
import com.module.core.models.service.Candidate;
import com.module.core.models.view.ProfileSearchParams;
import com.module.core.models.view.SegmentForView;

@Service
@Transactional
public class ProfileService {
	
	private static final int PROFILE_LIMIT = 10;
	private static final int SUGGEST_LIMIT = 6;

	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private SegmentRepository segmentRepository;
	
	@Autowired
	private RecommendationService recommendationService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private EntityService entityService;
	
	@Autowired
	private ServiceService serviceService;
	
	public Profile getFullProfile (String id) {
		
		BasicProfile basicProfile = profileRepository.getBasicProfile(id);
		Profile profile;
		if (basicProfile == null) {
			// Empty basic profile
			profile = new Profile();
			profile.setId(id);
			profile.setAboutMe(new AboutMe(UUID.randomUUID().toString().substring(0,18), null, null));
			profile.setAddress(new Address());
			profile.setType(Type.TALENT);
			profileRepository.createBasicProfile(profile);
			profileRepository.createAboutMe(profile.getAboutMe(), id);
			profile.setExperience(Collections.emptyList());
			profile.setStudies(Collections.emptyList());
			profile.setSkills(Collections.emptyList());
			profile.setRecommendations(Collections.emptyList());
			profile.setReviews(Collections.emptyList());
			profile.setContacts(Collections.emptyList());
			return profile;
		}
		
		profile = new Profile(basicProfile.getId(),basicProfile.getName(), basicProfile.getAboutMe(), basicProfile.getProfilePicture(), basicProfile.getBirthDate(),
				basicProfile.getEmail(), basicProfile.getPhone(), basicProfile.getAddress(), basicProfile.getWeb(),null, null, null,null, null, null, basicProfile.getType(),
				basicProfile.getSalary(), basicProfile.getAverageReview(), basicProfile.getLikeQty());
		
		List<Segment> segments = segmentRepository.getAllSegment(id);

		Predicate<Segment> filterExperience = segment -> segment.getType() == Segment.Type.EXPERIENCE;
		profile.setExperience(segments.stream().filter(filterExperience).sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate())).collect(Collectors.toList()));

		Predicate<Segment> filterEducation = segment -> segment.getType() == Segment.Type.EDUCATION;
		profile.setStudies(segments.stream().filter(filterEducation).sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate())).collect(Collectors.toList()));
		
		List<Entity> skills = entityService.getSkills(id);
		profile.setSkills(skills);
			
		List<Recommendation> recommendation = recommendationService.getRecommendations(id);
		profile.setRecommendations(recommendation);
		
		List<Review> reviews = reviewService.getReviews(Review.Type.OWNER.toString(), id);
		profile.setReviews(reviews);

		profile.setContacts(getContacts("", PROFILE_LIMIT, id));
		
		return profile;	
	}
	
	public PublicProfile getPublicProfile(String id, String profId) throws NotFoundException {
		BasicProfile basicProfile = getBasicProfile(id);
		PublicProfile profile = new PublicProfile(basicProfile.getId(),basicProfile.getName(), basicProfile.getAboutMe(), basicProfile.getProfilePicture(), basicProfile.getBirthDate(), 
				basicProfile.getEmail(), basicProfile.getPhone(), basicProfile.getAddress(), basicProfile.getWeb(),null, null, null,null, null, basicProfile.getType(), basicProfile.getSalary(), 
				null, basicProfile.getAverageReview(), basicProfile.getLikeQty());
		
		List<Segment> segments = segmentRepository.getAllSegment(id);

		Predicate<Segment> filterExperience = segment -> segment.getType() == Segment.Type.EXPERIENCE;
		profile.setExperience(segments.stream().filter(filterExperience).sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate())).collect(Collectors.toList()));

		Predicate<Segment> filterEducation = segment -> segment.getType() == Segment.Type.EDUCATION;
		profile.setStudies(segments.stream().filter(filterEducation).sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate())).collect(Collectors.toList()));
			
		List<Recommendation> recommendations = recommendationService.getRecommendations(id);
		profile.setRecommendations(recommendations);

		List<Review>reviews = reviewService.getReviews(Review.Type.OWNER.toString(), id);
		profile.setReviews(reviews);

		profile.setStatus(contactService.contactStatus(id, profId));
		
		profile.setSkills(entityService.getSkills(id));
		
		return profile;	
	}
	
	public BasicProfile getBasicProfile (String id) throws NotFoundException {
		BasicProfile bp = profileRepository.getBasicProfile(id);
		if (bp == null) {
			throw new NotFoundException();
		}
		List<Entity> skills = entityService.getSkills(id);
		bp.setSkills(skills);
		return bp;
	}
	

	public LiteProfile getLiteProfile(String id) throws NotFoundException {
		LiteProfile lp = profileRepository.getLiteProfile(id);
		if (lp == null) {
			throw new NotFoundException();
		}
		return lp;
	}
	
	public void updateBasicProfile(BasicProfile basicprofile) {
		profileRepository.updateBasicProfile(basicprofile);
		profileRepository.updateAboutMe(basicprofile.getAboutMe(), basicprofile.getId());
	}
	
	public List<LiteProfile> getContacts(String fragment, int limit, String id) {
		return profileRepository.getContacts(fragment, limit, id);
	}
	
	public List<LiteProfile> searchProfiles(ProfileSearchParams params, String idUser) {
		return profileRepository.searchProfiles(params, idUser);
	}
	
	//**********EXPERIENCE**********
	//**********EDUCATION**********
	public List<Segment> getSegment(String profileId, Segment.Type type) {
		return segmentRepository.getSegment(profileId, type);
	}
	
	public void updateSegment(Segment segment, String profileId) {
		segmentRepository.updateSegment(segment, profileId);
	}
	
	public void createSegment(SegmentForView segmentForView, String profileId, Segment.Type type) {
		Segment segment = new Segment();
		String id = UUID.randomUUID().toString().substring(0,18);
		segment.setId(id);
		segment.setPosition(segmentForView.getPosition());
		segment.setCompany(segmentForView.getCompany());
		segment.setStartDate(segmentForView.getStartDate());
		segment.setEndDate(segmentForView.getEndDate());
		segment.setAddress(new Address(segmentForView.getAddressId(), null, null));
		segment.setDescription(segmentForView.getDescription());
		segment.setType(type);
		segmentRepository.createSegment(segment, profileId);
	}
	
	public void deleteSegment(String id) {
		segmentRepository.deleteSegment(id);
	}
	
	//**********MAIL**********
	
	public String getEmail(String id) {
		return profileRepository.getEmail(id);
	}

	public List<LiteProfile> suggestProfilesForService (String serviceId) throws NotFoundException {
		com.module.core.models.service.Service service = serviceService.getService(null, serviceId);
		return findSuggestedProfiles(service);
	}
	
	public List<LiteProfile> findSuggestedProfiles (com.module.core.models.service.Service service) {
		HashMap<String, Integer> talentRatingMap = new HashMap<>();
		List<LiteProfile> foundProfiles = new ArrayList<>();
		ProfileSearchParams paramsAddress = new ProfileSearchParams();
		
		//Recover from data base profiles that match with address
		paramsAddress.setAddressId(service.getAddress().getId());
		paramsAddress.setType(service.getCandidateType() != Type.DUAL ? service.getCandidateType() : null);
		List<LiteProfile> addressMatchProfiles = profileRepository.searchProfiles(paramsAddress, service.getOrigin().getId());
		if (addressMatchProfiles != null && !addressMatchProfiles.isEmpty()){
			foundProfiles.addAll(addressMatchProfiles);
		}
		
		//Recover from data base profiles that match with any skill
		//Profiles that have more than one skill will be added one time for each skill that match
		List<Entity> skills = service.getSkills();
		if (skills != null && !skills.isEmpty()) {
			foundProfiles.addAll(findProfilesWithSkills(service, skills));
			
		}
		
		//Fill the map with the profile IDs and the number of repetitions in found profiles
		if (foundProfiles != null && !foundProfiles.isEmpty()) {
			for (LiteProfile lp : foundProfiles) {
				Integer count = talentRatingMap.containsKey(lp.getId()) ? talentRatingMap.get(lp.getId())+1 : 1;
				talentRatingMap.put(lp.getId(), count);		
			}
		}
		
		// Remove profiles that are already candidates for the service
		List<Candidate> candidates = serviceService.getCandidates(service.getId());
		if (candidates != null && !candidates.isEmpty()) {
			candidates.forEach(c -> Optional.ofNullable(talentRatingMap.get(c.getProfile().getId())).ifPresent(id -> talentRatingMap.remove(c.getProfile().getId())));
		}
				
		//Order the map starting from the highest repetition value
		LinkedHashMap<String, Integer> orderedTalentRatingMap = talentRatingMap.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
		
		//Recover from data base lite profiles from the highest value to the suggest limit
		List<LiteProfile> orderedProfiles = new ArrayList<>();
		for (String profileId : orderedTalentRatingMap.keySet()) {
			orderedProfiles.add(profileRepository.getLiteProfile(profileId));
			if (orderedProfiles.size()==SUGGEST_LIMIT) {
				break;
			}
		}
		return orderedProfiles;
	}
	
	public void updateProfileImage (String imageUrl, String profileId) {
		profileRepository.updateProfileImage(imageUrl, profileId);
	}
	
	
	private List<LiteProfile> findProfilesWithSkills (com.module.core.models.service.Service service, List<Entity> skills) {
		ProfileSearchParams paramsSkill = new ProfileSearchParams();
		paramsSkill.setType(service.getCandidateType() != Type.DUAL ? service.getCandidateType() : null);
		List<LiteProfile> result = new ArrayList<>();
		for (Entity skill : skills) {
			paramsSkill.setSkillId(skill.getId());
			List<LiteProfile> skillMatchProfiles = profileRepository.searchProfiles(paramsSkill, service.getOrigin().getId());
			if (skillMatchProfiles != null && !skillMatchProfiles.isEmpty()) {
				result.addAll(skillMatchProfiles);
			}
		}
		return result;
	}


}
