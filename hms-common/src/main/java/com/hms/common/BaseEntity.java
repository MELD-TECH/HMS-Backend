package com.hms.common;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.UUID)
	    private UUID id;

	    @CreatedDate
	    @Column(name = "created_at", nullable = false, updatable = false)
	    private LocalDateTime createdAt;

	    @LastModifiedDate
	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;

	    @CreatedBy
	    @Column(name = "created_by", length = 100, updatable = false)
	    private String createdBy;

	    @LastModifiedBy
	    @Column(name = "updated_by", length = 100)
	    private String updatedBy;

	    @Version
	    @Column(name = "version")
	    private Long version;
	}

