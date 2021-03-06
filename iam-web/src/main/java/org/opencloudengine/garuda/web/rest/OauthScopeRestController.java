package org.opencloudengine.garuda.web.rest;


import org.opencloudengine.garuda.web.console.oauthscope.OauthScope;
import org.opencloudengine.garuda.web.console.oauthscope.OauthScopeService;
import org.opencloudengine.garuda.web.console.oauthuser.OauthUser;
import org.opencloudengine.garuda.web.console.oauthuser.OauthUserService;
import org.opencloudengine.garuda.web.management.Management;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/rest/v1")
public class OauthScopeRestController {
    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private OauthScopeService oauthScopeService;

    @Autowired
    private RestAuthService restAuthService;

    @RequestMapping(value = "/scope", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthScope>> listAllScopes(HttpServletRequest request) {
        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<OauthScope> oauthScopes = oauthScopeService.selectByGroupId(management.getId());
            if (oauthScopes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(oauthScopes, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/scope/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<OauthScope> getScope(HttpServletRequest request, @PathVariable("id") long id) {

        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            OauthScope oauthScope = oauthScopeService.selectByGroupIdAndId(management.getId(), id);
            if (oauthScope == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(oauthScope, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/scope", method = RequestMethod.POST)
    public ResponseEntity<Void> createScope(HttpServletRequest request, @RequestBody OauthScope oauthScope, UriComponentsBuilder ucBuilder) {
        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {

            OauthScope existScope = oauthScopeService.selectByGroupIdAndName(management.getId(), oauthScope.getName());
            if (existScope != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            OauthScope createdScope = oauthScopeService.createScope(management.getId(), oauthScope.getName(), oauthScope.getDescription(), oauthScope.getAdditionalInformation());

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/rest/v1/scope/{id}").buildAndExpand(createdScope.getId()).toUri());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/scope/{id}", method = RequestMethod.PUT)
    public ResponseEntity<OauthScope> updateScope(HttpServletRequest request, @PathVariable("id") long id, @RequestBody OauthScope oauthScope) {

        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            OauthScope currentScope = oauthScopeService.selectByGroupIdAndId(management.getId(), id);

            if (currentScope == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            oauthScope.setId(currentScope.getId());
            oauthScopeService.updateById(oauthScope);

            currentScope = oauthScopeService.selectByGroupIdAndId(management.getId(), id);
            return new ResponseEntity<>(currentScope, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/scope/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<OauthScope> deleteScope(HttpServletRequest request, @PathVariable("id") long id) {

        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            OauthScope currentScope = oauthScopeService.selectByGroupIdAndId(management.getId(), id);

            if (currentScope == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            oauthScopeService.deleteById(currentScope.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/search/scope", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthScope>> searchScope(HttpServletRequest request,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String description,
                                                      @RequestParam(required = false) String additionalInformation) {

        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            OauthScope oauthScope = new OauthScope();
            oauthScope.setGroupId(management.getId());
            oauthScope.setName(name);
            oauthScope.setDescription(description);
            oauthScope.setAdditionalInformation(additionalInformation);

            List<OauthScope> oauthScopes = oauthScopeService.selectByCondition(oauthScope);

            if (oauthScopes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(oauthScopes, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
