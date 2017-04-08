package com.kinancity.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				// Spring Security should completely ignore URLs starting with /api/
				.antMatchers("/api/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**").permitAll();
		// http
		// .authorizeRequests()
		// .antMatchers("/v2/api-docs").permitAll()
		// .antMatchers("/swagger-resources").permitAll()
		// .antMatchers("/swagger-ui.html").permitAll()
		// .antMatchers("/webjars/**").permitAll()
		// .antMatchers("/configuration/ui/**").permitAll()
		// .antMatchers("/public/**").permitAll().anyRequest().hasRole("USER")
		// .antMatchers("/console/**").hasRole("ADMIN");

		http.headers().frameOptions().disable();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.inMemoryAuthentication()
				.withUser("admin").password("admin").roles("ADMIN");
	}
}
