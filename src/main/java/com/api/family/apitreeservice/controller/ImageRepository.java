package com.api.family.apitreeservice.controller;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.family.apitreeservice.model.postgres.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
