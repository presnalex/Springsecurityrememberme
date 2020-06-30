package com.ducat;
import com.ducat.service.AuthenticationService;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
//@PropertySource("classpath:application.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	Environment env;
	@Autowired
        @Lazy(value=true)
	AuthenticationService authenticationService;	
        @Autowired
        private DataSource dataSource;
           
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//authorize requests
		http.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
//		.access("hasRole('ROLE_ADMIN')")
                        .anyRequest().authenticated();
		
		//login configuration
		http.formLogin().  
        loginPage("/customlogin").permitAll().
        loginProcessingUrl("/appLogin").
        usernameParameter("app_username").
        passwordParameter("app_password").
        defaultSuccessUrl("/home")
                        
            .and()
		
		//remember me configuration
		.rememberMe(). 
		tokenRepository(persistentTokenRepository()).
        rememberMeParameter("remember-me-param").
        rememberMeCookieName("my-remember-me").
        tokenValiditySeconds(86400)
		
		//logout configuration
        .and()
        .logout().    
		logoutUrl("/appLogout")
		.logoutSuccessUrl("/")
        .and()
        .csrf().disable();
                http.headers().frameOptions().sameOrigin();
	} 
        @Bean
        public PasswordEncoder delegatingPasswordEncoder() {
            PasswordEncoder encoder = new  org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1");
            return encoder;
        }
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

	        auth.userDetailsService(authenticationService).passwordEncoder(delegatingPasswordEncoder());
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}

}  
