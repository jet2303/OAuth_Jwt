package com.example.jwt_oauth.config.security.handler;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.jwt_oauth.config.OAuth2Config;
import com.example.jwt_oauth.config.security.util.CustomCookie;
import com.example.jwt_oauth.domain.mapping.TokenMapping;
import com.example.jwt_oauth.domain.user.Token;
import com.example.jwt_oauth.repository.auth.CustomAuthorizationRequestRepository;
import com.example.jwt_oauth.repository.auth.TokenRepository;
import com.example.jwt_oauth.service.user.auth.CustomTokenProviderService;

import lombok.RequiredArgsConstructor;
import static com.example.jwt_oauth.repository.auth.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@RequiredArgsConstructor
@Component
public class CustomSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;
    private final CustomTokenProviderService customTokenProviderService;
    private final TokenRepository tokenRepository;
    private final OAuth2Config oAuth2Config;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        // super.onAuthenticationSuccess(request, response, authentication);
        String targetUrl = determineTargetUrl(request, response, authentication);
        
        super.clearAuthenticationAttributes(request);
        customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
    
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
        // DefaultAssert.isAuthentication( !(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) );

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);

        Token token = Token.builder()
                                .userEmail(tokenMapping.getUserEmail())
                                .refreshToken(tokenMapping.getRefreshToken())
                                .build();
        tokenRepository.save(token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                                    .queryParam("token", tokenMapping.getAccessToken())
                                    .build()
                                    .toString();
    }
    
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response){
        super.clearAuthenticationAttributes(request);
        customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri){
        URI clientRedirectUri = URI.create(uri);
        return oAuth2Config.getOAuth2().getAuthorizedRedirectUris()
                            .stream()
                            .anyMatch(authorizaedRedirectUri -> {
                                URI authorizedUri = URI.create(authorizaedRedirectUri);
                                if(authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) 
                                        && (authorizedUri.getPort() == clientRedirectUri.getPort()) ){
                                    return true;
                                }
                                return false;
                            });
    }
}
