package org.opencloudengine.garuda.web.console.oauthuser;

import java.util.List;

public interface OauthUserService {

    OauthUser selectById(Long id);

    List<OauthUser> selectByGroupId(Long groupId);

    OauthUser selectByGroupIdAndUserName(Long groupId, String userName);

    OauthUser selectByGroupIdAndCredential(Long groupId, String userName, String userPassword);

    OauthUser selectByGroupIdAndId(Long groupId, Long id);

    int updateById(Long id, String userName, String userPassword, Integer level, String additionalInformation);

    int updateById(OauthUser oauthUser);

    int deleteById(Long id);

    List<OauthUser> selectByCondition(OauthUser oauthUser);

    OauthUser createUser(Long groupId, String userName, String userPassword, Integer level, String additionalInformation);

    OauthSessionToken validateSessionToken(String sessionToken) throws Exception;

    OauthScopeToken validateScopeToken(String scopeToken) throws Exception;

    OauthSessionToken generateSessionToken(String managementKey, String userName, String userPassword) throws Exception;

    OauthScopeToken generateScopeToken(String managementKey, String userName, String clientKey, String scopes) throws Exception;
}
