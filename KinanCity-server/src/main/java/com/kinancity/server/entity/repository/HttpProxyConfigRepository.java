package com.kinancity.server.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kinancity.server.entity.HttpProxyConfig;

public interface HttpProxyConfigRepository extends JpaRepository<HttpProxyConfig, Long> {

}
