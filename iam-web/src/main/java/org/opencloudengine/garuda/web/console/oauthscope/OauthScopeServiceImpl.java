package org.opencloudengine.garuda.web.console.oauthscope;

import org.opencloudengine.garuda.web.configuration.ConfigurationHelper;
import org.opencloudengine.garuda.web.console.oauthclient.OauthClientScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class OauthScopeServiceImpl implements OauthScopeService {
    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private OauthScopeRepository oauthScopeRepository;

    @Autowired
    ConfigurationHelper configurationHelper;

    @Override
    public OauthScope selectById(Long id) {
        return oauthScopeRepository.selectById(id);
    }

    @Override
    public List<OauthScope> selectByGroupId(Long groupId) {
        return oauthScopeRepository.selectByGroupId(groupId);
    }

    @Override
    public OauthScope selectByGroupIdAndName(Long groupId, String name) {
        return oauthScopeRepository.selectByGroupIdAndName(groupId, name);
    }

    @Override
    public OauthScope selectByGroupIdAndId(Long groupId, Long id) {
        return oauthScopeRepository.selectByGroupIdAndId(groupId, id);
    }

    @Override
    public List<OauthScope> selectByCondition(OauthScope oauthScope) {
        return oauthScopeRepository.selectByCondition(oauthScope);
    }

    @Override
    public int updateById(Long id, String name, String description, String additionalInformation) {
        OauthScope OauthScope = new OauthScope();
        OauthScope.setId(id);
        OauthScope.setName(name);
        OauthScope.setDescription(description);
        OauthScope.setAdditionalInformation(additionalInformation);
        return oauthScopeRepository.updateById(OauthScope);
    }

    @Override
    public int updateById(OauthScope oauthScope) {
        return oauthScopeRepository.updateById(oauthScope);
    }

    @Override
    public int deleteById(Long id) {
        return oauthScopeRepository.deleteById(id);
    }

    @Override
    public OauthScope createScope(Long groupId, String name, String description, String additionalInformation) {
        OauthScope OauthScope = new OauthScope();
        OauthScope.setGroupId(groupId);
        OauthScope.setName(name);
        OauthScope.setDescription(description);
        OauthScope.setAdditionalInformation(additionalInformation);
        oauthScopeRepository.insert(OauthScope);

        return OauthScope;
    }

    @Override
    public int insertClientScopes(OauthClientScopes oauthClientScopes) {
        return oauthScopeRepository.insertClientScopes(oauthClientScopes);
    }

    @Override
    public List<OauthScope> selectClientScopes(Long clientId) {
        return oauthScopeRepository.selectClientScopes(clientId);
    }

    @Override
    public OauthScope selectClientScopesByScopeId(Long clientId, Long scopeId) {
        return oauthScopeRepository.selectClientScopesByScopeId(clientId, scopeId);
    }

    @Override
    public int deleteClientScopes(Long clientId) {
        return oauthScopeRepository.deleteClientScopes(clientId);
    }

    @Override
    public int deleteClientScopesByScopeId(Long clientId, Long scopeId) {
        return oauthScopeRepository.deleteClientScopesByScopeId(clientId, scopeId);
    }
}
