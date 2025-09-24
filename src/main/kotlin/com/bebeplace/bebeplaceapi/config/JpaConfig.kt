package com.bebeplace.bebeplaceapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.bebeplace.bebeplaceapi"])
class JpaConfig