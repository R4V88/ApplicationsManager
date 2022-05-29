package com.applicationsmanager.applicationsmanager.applications.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class History {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Application application;

    @Enumerated(EnumType.STRING)
    Status status;

    @CreatedDate
    LocalDateTime createdAt;

    public History(Application application, Status status) {
        this.application = application;
        this.status = status;
    }
}
