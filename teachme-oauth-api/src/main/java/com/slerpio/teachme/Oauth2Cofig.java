package com.slerpio.teachme;

import com.slerpio.teachme.security.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyPair;

/**
 * The configuration file for spring oauth 2 security
 *
 * @author kiditz
 */
@Configuration
public class Oauth2Cofig {
    Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    ClientService clientService;

    @Configuration
    @EnableAuthorizationServer
    protected class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            log.info("ENABLE OAUTH SERVER");
            clients.withClientDetails(clientService);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)  {
            endpoints.authenticationManager(authenticationManager).tokenServices(tokenServices());
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security)  {
            security.tokenKeyAccess("permitAll()").checkTokenAccess("hasRole('CLIENT')");
        }

        @Bean
        public DefaultTokenServices tokenServices(){
            DefaultTokenServices services = new DefaultTokenServices();
            services.setRefreshTokenValiditySeconds(3600 * 24);
            services.setSupportRefreshToken(true);
            services.setAccessTokenValiditySeconds(3600);
            services.setTokenStore(tokenStore());
            services.setTokenEnhancer(jwtAccessTokenConverter());
            return services;
        }

        @Bean
        @Qualifier("tokenStore")
        public TokenStore tokenStore() {
            return new JwtTokenStore(jwtAccessTokenConverter());
        }

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "password".toCharArray()).getKeyPair("jwt");
            converter.setKeyPair(keyPair);
            return converter;
        }
    }
}
