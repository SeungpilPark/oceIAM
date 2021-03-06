package org.opencloudengine.garuda.web.console.oauthuser;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.opencloudengine.garuda.common.exception.ServiceException;
import org.opencloudengine.garuda.util.JsonUtils;
import org.opencloudengine.garuda.util.JwtUtils;
import org.opencloudengine.garuda.web.configuration.ConfigurationHelper;
import org.opencloudengine.garuda.web.console.oauthclient.OauthClient;
import org.opencloudengine.garuda.web.console.oauthclient.OauthClientService;
import org.opencloudengine.garuda.web.management.Management;
import org.opencloudengine.garuda.web.management.ManagementRepository;
import org.opencloudengine.garuda.web.management.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OauthUserServiceImpl implements OauthUserService {
    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private OauthUserRepository oauthUserRepository;

    @Autowired
    ConfigurationHelper configurationHelper;

    @Autowired
    ManagementService managementService;

    @Autowired
    OauthClientService clientService;

    @Override
    public OauthUser selectById(Long id) {
        return oauthUserRepository.selectById(id);
    }

    @Override
    public List<OauthUser> selectByGroupId(Long groupId) {
        return oauthUserRepository.selectByGroupId(groupId);
    }

    @Override
    public OauthUser selectByGroupIdAndUserName(Long groupId, String userName) {
        return oauthUserRepository.selectByGroupIdAndUserName(groupId, userName);
    }

    @Override
    public OauthUser selectByGroupIdAndCredential(Long groupId, String userName, String userPassword) {
        return oauthUserRepository.selectByGroupIdAndCredential(groupId, userName, userPassword);
    }

    @Override
    public OauthUser selectByGroupIdAndId(Long groupId, Long id) {
        return oauthUserRepository.selectByGroupIdAndId(groupId, id);
    }

    @Override
    public int updateById(Long id, String userName, String userPassword, Integer level, String additionalInformation) {
        OauthUser oauthUser = new OauthUser();
        oauthUser.setId(id);
        oauthUser.setUserName(userName);
        oauthUser.setUserPassword(userPassword);
        oauthUser.setLevel(level);
        oauthUser.setAdditionalInformation(additionalInformation);
        return oauthUserRepository.updateById(oauthUser);
    }

    @Override
    public int updateById(OauthUser oauthUser) {
        return oauthUserRepository.updateById(oauthUser);
    }

    @Override
    public int deleteById(Long id) {
        return oauthUserRepository.deleteById(id);
    }

    @Override
    public List<OauthUser> selectByCondition(OauthUser oauthUser) {
        return oauthUserRepository.selectByCondition(oauthUser);
    }

    @Override
    public OauthUser createUser(Long groupId, String userName, String userPassword, Integer level, String additionalInformation) {
        OauthUser oauthUser = new OauthUser();
        oauthUser.setGroupId(groupId);
        oauthUser.setUserName(userName);
        oauthUser.setUserPassword(userPassword);
        oauthUser.setLevel(level);
        oauthUser.setAdditionalInformation(additionalInformation);
        oauthUserRepository.insert(oauthUser);
        return oauthUser;
    }

    @Override
    public OauthSessionToken validateSessionToken(String sessionToken) throws Exception {

        OauthSessionToken oauthSessionToken = new OauthSessionToken();

        JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(sessionToken);

        //세션토큰은 이슈발급자가 매니지먼트 키
        String managementKey = jwtClaimsSet.getIssuer();

        Management management = managementService.selectByKey(managementKey);
        String sharedSecret = management.getGroupJwtSecret();

        Map context = (Map) jwtClaimsSet.getClaim("context");
        String userName = (String) context.get("userName");

        //만료시간
        Date issueTime = jwtClaimsSet.getIssueTime();
        Date expirationTime = new Date(issueTime.getTime() + management.getSessionTokenLifetime() * 1000);

        boolean validated = JwtUtils.validateToken(sessionToken, sharedSecret, expirationTime);

        oauthSessionToken.setToken(sessionToken);
        oauthSessionToken.setValidated(validated);
        oauthSessionToken.setUserName(userName);

        return oauthSessionToken;
    }

    @Override
    public OauthScopeToken validateScopeToken(String scopeToken) throws Exception {
        OauthScopeToken oauthScopeToken = new OauthScopeToken();

        JWTClaimsSet jwtClaimsSet = JwtUtils.parseToken(scopeToken);

        //세션토큰은 이슈발급자가 매니지먼트 키
        String managementKey = jwtClaimsSet.getIssuer();

        Management management = managementService.selectByKey(managementKey);
        String sharedSecret = management.getGroupJwtSecret();

        Map context = (Map) jwtClaimsSet.getClaim("context");
        String userName = (String) context.get("userName");
        String clientKey = (String) context.get("clientKey");
        String scopes = (String) context.get("scopes");

        //만료시간
        Date issueTime = jwtClaimsSet.getIssueTime();
        Date expirationTime = new Date(issueTime.getTime() + management.getScopeCheckLifetime() * 1000);

        boolean validated = JwtUtils.validateToken(scopeToken, sharedSecret, expirationTime);

        oauthScopeToken.setToken(scopeToken);
        oauthScopeToken.setValidated(validated);
        oauthScopeToken.setUserName(userName);
        oauthScopeToken.setClientKey(clientKey);
        oauthScopeToken.setScopes(scopes);

        return oauthScopeToken;
    }

    @Override
    public OauthSessionToken generateSessionToken(String managementKey, String userName, String userPassword) throws Exception {

        Management management = managementService.selectByKey(managementKey);
        if (management == null) {
            return null;
        }

        OauthUser oauthUser = this.selectByGroupIdAndCredential(management.getId(), userName, userPassword);
        if (oauthUser == null) {
            return null;
        }

        //발급 시간
        Date issueTime = new Date();

        //만료시간
        Date expirationTime = new Date(new Date().getTime() + management.getSessionTokenLifetime() * 1000);

        //발급자
        String issuer = management.getGroupKey();

        //시그네이쳐 설정
        String sharedSecret = management.getGroupJwtSecret();
        JWSSigner signer = new MACSigner(sharedSecret);

        //콘텍스트 설정
        Map context = new HashMap();
        context.put("managementKey", managementKey);
        context.put("userName", userName);

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        JWTClaimsSet claimsSet = builder
                .issuer(issuer)
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .claim("context", context).build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);

        String sessionToken = signedJWT.serialize();

        OauthSessionToken oauthSessionToken = new OauthSessionToken();

        oauthSessionToken.setToken(sessionToken);
        oauthSessionToken.setUserName(userName);
        oauthSessionToken.setValidated(true);

        return oauthSessionToken;
    }

    @Override
    public OauthScopeToken generateScopeToken(String managementKey, String userName, String clientKey, String scopes) throws Exception {
        Management management = managementService.selectByKey(managementKey);
        if (management == null) {
            return null;
        }

        OauthClient oauthClient = clientService.selectByClientKey(clientKey);
        if (oauthClient == null) {
            return null;
        }

        //발급 시간
        Date issueTime = new Date();

        //만료시간
        Date expirationTime = new Date(new Date().getTime() + management.getScopeCheckLifetime() * 1000);

        //발급자
        String issuer = management.getGroupKey();

        //시그네이쳐 설정
        String sharedSecret = management.getGroupJwtSecret();
        JWSSigner signer = new MACSigner(sharedSecret);

        //콘텍스트 설정
        Map context = new HashMap();
        context.put("managementKey", managementKey);
        context.put("userName", userName);
        context.put("clientKey", clientKey);
        context.put("scopes", scopes);

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        JWTClaimsSet claimsSet = builder
                .issuer(issuer)
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .claim("context", context).build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);

        String scopeToken = signedJWT.serialize();

        OauthScopeToken oauthScopeToken = new OauthScopeToken();

        oauthScopeToken.setToken(scopeToken);
        oauthScopeToken.setUserName(userName);
        oauthScopeToken.setValidated(true);
        oauthScopeToken.setClientKey(clientKey);
        oauthScopeToken.setScopes(scopes);

        return oauthScopeToken;
    }
}
