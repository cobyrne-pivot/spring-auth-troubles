package com.example.authtroubles

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val DEFAULT_FILTER_CHAIN_ORDER = 100

@SpringBootApplication
class AuthTroublesApplication {
    @EnableWebSecurity
    class SecurityConfiguration {
        @Bean
        fun inMemoryUserDetailsService(): UserDetailsService {
            val manager = InMemoryUserDetailsManager()

            manager.createUser(User.withDefaultPasswordEncoder()
                    .username("username")
                    .password("password")
                    .roles("USER")
                    .build()
            )

            manager.createUser(User.withDefaultPasswordEncoder()
                    .username("guest")
                    .password("password")
                    .roles("GUEST")
                    .build()
            )

            return manager
        }

        @Configuration
        @Order(DEFAULT_FILTER_CHAIN_ORDER - 2)
        class ApiConfiguration : WebSecurityConfigurerAdapter() {
            override fun configure(http: HttpSecurity) {
                http.antMatcher("/api/**")
                        .authorizeRequests()
                            .anyRequest().hasRole("USER")
                            .and()
                        .csrf().disable()
                        .httpBasic()
            }
        }

        @Configuration
        @Order(DEFAULT_FILTER_CHAIN_ORDER - 1)
        class OktaWebConfiguration : WebSecurityConfigurerAdapter() {
            override fun configure(http: HttpSecurity) {
                http.antMatcher("/**")
                        .authorizeRequests()
                           .anyRequest().authenticated()
                            .and()
                        .oauth2Login()
                            .loginPage("$DEFAULT_AUTHORIZATION_REQUEST_BASE_URI/okta")
            }
        }
    }

    @RestController
    @RequestMapping("/api")
    class ApiController {
        @RequestMapping("/foo")
        fun foo(): ResponseEntity<Map<String, String>> {
            return ResponseEntity.ok(mapOf("foo" to "bar"))
        }
    }

    @Controller
    class WebController {
        @RequestMapping(value = "/home", produces = ["text/plain"])
        fun foo(): String {
            return "bar"
        }
    }
}

fun main(args: Array<String>) {
    runApplication<AuthTroublesApplication>(*args)
}
