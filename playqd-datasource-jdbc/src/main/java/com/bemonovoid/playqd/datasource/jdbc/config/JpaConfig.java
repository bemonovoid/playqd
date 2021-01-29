package com.bemonovoid.playqd.datasource.jdbc.config;

import com.bemonovoid.playqd.core.helpers.PackageHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = PackageHelper.BASE_PACKAGE + ".datasource.jdbc.repository")
@EnableTransactionManagement
@EnableJpaAuditing
class JpaConfig {

}
