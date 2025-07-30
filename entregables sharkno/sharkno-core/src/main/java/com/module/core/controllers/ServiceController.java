package com.module.core.controllers;

import java.util.Collections;
import java.util.List;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Session;
import com.module.core.models.service.AppliedService;
import com.module.core.models.service.BasicService;
import com.module.core.models.service.BasicService.Status;
import com.module.core.models.service.Candidate;
import com.module.core.models.service.Service;
import com.module.core.models.view.ExternalServiceForView;
import com.module.core.models.view.ServiceForView;
import com.module.core.models.view.ServiceSearchParams;
import com.module.core.services.EntityService;
import com.module.core.services.ServiceService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.SERVICE_CONTROLLER_TAG})
@RequestMapping("service")
@CrossOrigin(origins = "*")
public class ServiceController extends BaseController{

	@Autowired
	ServiceService serviceService;
	
	@Autowired
	EntityService entityService;
	
	@PutMapping(consumes="application/json")
	@ApiOperation(value="Create Service", authorizations = {@Authorization(value="basicAuth")})
	public String createService(@RequestBody ServiceForView service, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.createService(service, idUser(session));
		}
		return null;
	}
	
	@PutMapping(path="/external", consumes="application/json")
	@ApiOperation(value="Create External Service", authorizations = {@Authorization(value="basicAuth")})
	public String createExternalService(@RequestBody ExternalServiceForView externalService, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.createExternalService(externalService, session);
		}
		return null;
	}
	
	@PostMapping(consumes="application/json")
	@ApiOperation(value="Update Service", authorizations = {@Authorization(value="basicAuth")})
	public void updateService(@RequestBody Service service, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.updateService(service);
		}
	}
	
	@GetMapping("/{serviceId}")
	@ApiOperation(value="Get Service", authorizations = {@Authorization(value="basicAuth")})
	public AppliedService getService(@PathVariable String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.getService(idUser(session), serviceId);
			
		}
		return new AppliedService();
	}
	
	@PostMapping("/status")
	@ApiOperation(value="Update Service Status", authorizations = {@Authorization(value="basicAuth")})
	public void updateServiceStatus(@RequestParam String serviceId, @RequestParam Status status, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.updateServiceStatus(serviceId, status, idUser(session));
		}
	}
	
	@PostMapping("/candidate")
	@ApiOperation(value="Update Candidate Status", authorizations = {@Authorization(value="basicAuth")})
	public void updateCandidateStatus(@RequestParam String candidateId, @RequestParam Candidate.Status status, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.updateCandidateStatus(candidateId, status, session);
		}
	}

	@GetMapping("/openservices")
	@ApiOperation(value="Search for User's Open Services", authorizations = {@Authorization(value="basicAuth")})
	public List<BasicService> searchOpenServices(@RequestParam String titleFragment, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.searchServicesByStatus(idUser(session), titleFragment, Status.OPEN);
		}
		return Collections.emptyList();
	}
	
	@PostMapping(path="/search", consumes="application/json")
	@ApiOperation(value="Search Services", authorizations = {@Authorization(value="basicAuth")})
	public List<BasicService> searchServices(@RequestBody ServiceSearchParams params, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.searchServices(params, idUser(session));
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/myservices")
	@ApiOperation(value="Get My Services", authorizations = {@Authorization(value="basicAuth")})
	public List<BasicService> getMyServices(@RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.getMyServices(idUser(session));
		}
		return Collections.emptyList();
	}
	
	@PostMapping("/apply/{serviceId}")
	@ApiOperation(value="Apply to Service", authorizations = {@Authorization(value="basicAuth")})
	public void joinService(@PathVariable String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.addCandidateToService(session, serviceId);
		}
	}
		
	@GetMapping("/status/{status}")
	@ApiOperation(value="Get Services By Status", authorizations = {@Authorization(value="basicAuth")})
	public List<BasicService> getByStatusServices(@PathVariable Status status, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.getByStatusServices(status, idUser(session));
		}
		return Collections.emptyList();
	}
	
	@GetMapping(path="/completed/{profileId}")
	@ApiOperation(value="Get Completed Services", authorizations = {@Authorization(value="basicAuth")})
	public List<BasicService> getCompletedServices(@PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.getCompletedServices(profileId);
		}
		return Collections.emptyList();
	}
	
	@GetMapping(path="/active/{profileId}")
	@ApiOperation(value="Get Active Services", authorizations = {@Authorization(value="basicAuth")})
	public List<BasicService> getActiveServices(@PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return serviceService.getActiveServices(profileId);
		}
		return Collections.emptyList();
	}
	
	@PutMapping(path="/skill", consumes="application/json")
	@ApiOperation(value="Add Skill to Service", authorizations = {@Authorization(value="basicAuth")})
	public void addSkill(@RequestBody String skillId,  @RequestParam String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.createEntityRelation(skillId, serviceId, "SKILL");
		}
	}
	
	@DeleteMapping(path="/skill")
	@ApiOperation(value="Delete Skill from Service. Not a SOFT DELETE \uD83D\uDC40",
		notes ="Use with CAUTION \uD83D\uDC40",
		authorizations = {@Authorization(value="basicAuth")})
	public void deleteMySkill(@RequestParam String skillId, @RequestParam String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.deleteEntityRelation(serviceId, skillId);
		}
	}

	@PutMapping(path="/product", consumes="application/json")
	@ApiOperation(value="Add Product to Service", authorizations = {@Authorization(value="basicAuth")})
	public void addProduct(@RequestBody String productId, @RequestParam String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.addProductToService(productId, serviceId);
		}
	}
	
	@DeleteMapping(path="/product")
	@ApiOperation(value="Delete Product from Service. Not a SOFT DELETE \uD83D\uDC40",
		notes = "Use with CAUTION \uD83D\uDC40",authorizations = {@Authorization(value="basicAuth")})
	public void deleteProductFromService(@RequestParam String productId, @RequestParam String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.deleteProductFromService(productId, serviceId);
		}
	}
	
	@PostMapping(path="/external/accept/{serviceId}")
	@ApiOperation(value="Accept External Service From Business", authorizations = {@Authorization(value="basicAuth")})
	public void acceptExternalService(@PathVariable String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.updateServiceStatus(serviceId, BasicService.Status.EXTERNAL_COMPLETED, idUser(session));
		}
	}
	
	@PostMapping(path="/external/reject/{serviceId}")
	@ApiOperation(value="Reject External Service From Business", authorizations = {@Authorization(value="basicAuth")})
	public void rejectExternalService(@PathVariable String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			serviceService.updateServiceStatus(serviceId, BasicService.Status.EXTERNAL_REJECTED, idUser(session));
		}
	}
}
