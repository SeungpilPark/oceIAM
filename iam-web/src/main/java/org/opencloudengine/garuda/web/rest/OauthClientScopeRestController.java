package org.opencloudengine.garuda.web.rest;


import org.opencloudengine.garuda.web.console.oauthclient.OauthClient;
import org.opencloudengine.garuda.web.console.oauthclient.OauthClientScopes;
import org.opencloudengine.garuda.web.console.oauthclient.OauthClientService;
import org.opencloudengine.garuda.web.console.oauthscope.OauthScope;
import org.opencloudengine.garuda.web.console.oauthscope.OauthScopeService;
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
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/rest/v1")
public class OauthClientScopeRestController {
    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private OauthClientService oauthClientService;

    @Autowired
    private OauthScopeService oauthScopeService;

    @Autowired
    private RestAuthService restAuthService;

    @RequestMapping(value = "/client/{clientId}/scope", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<OauthScope>> listAllClientScopes(
            HttpServletRequest request, @PathVariable("clientId") long clientId) {
        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            OauthClient oauthClient = oauthClientService.selectByGroupIdAndId(management.getId(), clientId);
            if (oauthClient == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<OauthScope> oauthScopes = oauthScopeService.selectClientScopes(clientId);
            if (oauthScopes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(oauthScopes, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/client/{clientId}/scope/{scopeId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<OauthScope> getClientScope(
            HttpServletRequest request, @PathVariable("clientId") long clientId, @PathVariable("scopeId") long scopeId) {

        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            OauthClient oauthClient = oauthClientService.selectByGroupIdAndId(management.getId(), clientId);
            if (oauthClient == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            OauthScope oauthScope = oauthScopeService.selectClientScopesByScopeId(clientId, scopeId);
            if (oauthScope == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(oauthScope, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/client/{clientId}/scope/{scopeId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> createClientScope(
            HttpServletRequest request, @PathVariable("clientId") long clientId, @PathVariable("scopeId") long scopeId, UriComponentsBuilder ucBuilder) {
        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            OauthClient oauthClient = oauthClientService.selectByGroupIdAndId(management.getId(), clientId);
            if (oauthClient == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            OauthScope existScope = oauthScopeService.selectClientScopesByScopeId(clientId, scopeId);
            if (existScope != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            OauthClientScopes clientScopes = new OauthClientScopes();
            clientScopes.setClientId(clientId);
            clientScopes.setScopeId(scopeId);
            oauthScopeService.insertClientScopes(clientScopes);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/rest/v1/client/{clientId}/scope/${scopeId}").
                    buildAndExpand(clientScopes.getClientId(), clientScopes.getScopeId()).toUri());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/client/{clientId}/scope/{scopeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<OauthScope> deleteClientScope(
            HttpServletRequest request, @PathVariable("clientId") long clientId, @PathVariable("scopeId") long scopeId) {

        Management management = restAuthService.managementParser(request);
        if (management == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            OauthClient oauthClient = oauthClientService.selectByGroupIdAndId(management.getId(), clientId);
            if (oauthClient == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            OauthScope currentScope = oauthScopeService.selectClientScopesByScopeId(clientId, scopeId);

            if (currentScope == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            oauthScopeService.deleteClientScopesByScopeId(clientId, scopeId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
