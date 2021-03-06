package org.opencloudengine.garuda.web.oauth;

import org.opencloudengine.garuda.common.rest.Response;
import org.opencloudengine.garuda.util.ExceptionUtils;
import org.opencloudengine.garuda.util.JsonFormatterUtils;
import org.opencloudengine.garuda.util.JsonUtils;
import org.opencloudengine.garuda.web.console.oauthuser.OauthScopeToken;
import org.opencloudengine.garuda.web.console.oauthuser.OauthSessionToken;
import org.opencloudengine.garuda.web.console.oauthuser.OauthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/oauth")
public class OauthController {
    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private OauthService oauthService;

    @Autowired
    private OauthUserService oauthUserService;


    @RequestMapping(value = "/authorize", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView authorize(HttpSession session, HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        AuthorizeResponse authorizeResponse = oauthService.validateAuthorize(request);

        if (authorizeResponse.getError() != null) {
            Map map = new HashMap();
            map.put("error", authorizeResponse.getError());
            map.put("error_description", authorizeResponse.getError_description());
            map.put("state", authorizeResponse.getState());
            String marshal = JsonUtils.marshal(map);
            String prettyPrint = JsonFormatterUtils.prettyPrint(marshal);
            System.out.println(prettyPrint);
            response.getWriter().write(prettyPrint);

            oauthService.responseAuthorize(authorizeResponse);

            return null;
        } else {
            //인증 화면으로 넘어갈 것.
            ModelAndView mav = new ModelAndView("/auth/oauth-login");
            mav.addObject("authorizeResponse", authorizeResponse);
            mav.addObject("jsonAuthorizeResponse", JsonUtils.marshal(authorizeResponse));
            return mav;
        }
    }

    @RequestMapping(value = "/check_session", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response check_session(@RequestBody Map params) {
        Response response = new Response();
        try {

            //토큰값
            String token = (String) params.get("token");

            OauthSessionToken oauthSessionToken = oauthUserService.validateSessionToken(token);
            Map<String, Object> objectMap = JsonUtils.convertClassToMap(oauthSessionToken);

            response.getMap().putAll(objectMap);
            response.setSuccess(true);
        } catch (Exception ex) {
            response.setSuccess(false);
            response.getError().setMessage(ex.getMessage());
            if (ex.getCause() != null) response.getError().setCause(ex.getCause().getMessage());
            response.getError().setException(ExceptionUtils.getFullStackTrace(ex));
        }
        return response;
    }

    @RequestMapping(value = "/check_scope", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response check_scope(@RequestBody Map params) {
        Response response = new Response();
        try {

            //토큰값
            String token = (String) params.get("token");

            OauthScopeToken oauthScopeToken = oauthUserService.validateScopeToken(token);
            Map<String, Object> objectMap = JsonUtils.convertClassToMap(oauthScopeToken);

            response.getMap().putAll(objectMap);
            response.setSuccess(true);
        } catch (Exception ex) {
            response.setSuccess(false);
            response.getError().setMessage(ex.getMessage());
            if (ex.getCause() != null) response.getError().setCause(ex.getCause().getMessage());
            response.getError().setException(ExceptionUtils.getFullStackTrace(ex));
        }
        return response;
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response login(@RequestBody Map params) {
        Response response = new Response();
        try {

            String managementKey = (String) params.get("managementKey");
            String userName = (String) params.get("userName");
            String userPassword = (String) params.get("userPassword");
            OauthSessionToken oauthSessionToken = oauthUserService.generateSessionToken(managementKey, userName, userPassword);

            if (oauthSessionToken == null) {
                response.setSuccess(false);
            } else {
                response.setSuccess(true);
                response.getMap().putAll(JsonUtils.convertClassToMap(oauthSessionToken));
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.getError().setMessage(ex.getMessage());
            if (ex.getCause() != null) response.getError().setCause(ex.getCause().getMessage());
            response.getError().setException(ExceptionUtils.getFullStackTrace(ex));
        }
        return response;
    }

    @RequestMapping(value = "/create_scope_token", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response createScopeToken(@RequestBody Map params) {
        Response response = new Response();
        try {

            String managementKey = (String) params.get("managementKey");
            String clientKey = (String) params.get("clientKey");
            String userName = (String) params.get("userName");
            String scopes = (String) params.get("scopes");
            OauthScopeToken oauthScopeToken = oauthUserService.generateScopeToken(managementKey, userName, clientKey, scopes);

            if (oauthScopeToken == null) {
                response.setSuccess(false);
            } else {
                response.setSuccess(true);
                response.getMap().putAll(JsonUtils.convertClassToMap(oauthScopeToken));
            }
        } catch (Exception ex) {
            response.setSuccess(false);
            response.getError().setMessage(ex.getMessage());
            if (ex.getCause() != null) response.getError().setCause(ex.getCause().getMessage());
            response.getError().setException(ExceptionUtils.getFullStackTrace(ex));
        }
        return response;
    }


    @RequestMapping(value = "/authorize_redirect", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void authorize_redirect(HttpServletRequest request, HttpServletResponse response
    ) throws IOException {

        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> paramKeys = parameterMap.keySet();
        Iterator<String> iterator = paramKeys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String[] value = parameterMap.get(key);
            System.out.println(key + " : " + value);
        }
    }

    @RequestMapping(value = "/redirect", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void redirect(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam(defaultValue = "") String managementKey,
                         @RequestParam(defaultValue = "") String clientKey,
                         @RequestParam(defaultValue = "") String userName,
                         @RequestParam(defaultValue = "") String scopes,
                         @RequestParam(defaultValue = "") String responseType,
                         @RequestParam(defaultValue = "") String redirectUri,
                         @RequestParam(defaultValue = "") String state
    ) throws IOException {

        AuthorizeResponse authorizeResponse = oauthService.fetchAuthorize(managementKey, clientKey, userName, scopes, responseType, redirectUri, state);
        oauthService.processAuthorize(authorizeResponse, response);
    }

    @RequestMapping(value = "/access_token", method = RequestMethod.POST, produces = "application/json")
    public void accessToken(HttpServletRequest request, HttpServletResponse response
    ) throws IOException {

        oauthService.processAccessToken(request, response);
    }

    @RequestMapping(value = "/token_info", method = RequestMethod.GET, produces = "application/json")
    public void tokenInfo(HttpServletRequest request, HttpServletResponse response
    ) throws IOException {

        oauthService.processTokenInfo(request, response);
    }

    //TODO 어플리케이션 정리

    //TODO 다큐멘테이션과 동영상 제작
}
