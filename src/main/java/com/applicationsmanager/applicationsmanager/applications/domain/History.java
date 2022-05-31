package com.applicationsmanager.applicationsmanager.applications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class History {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Application application;

    String title;

    String content;

    String reason;

    Long uuid;

    @Enumerated(EnumType.STRING)
    Status status;

    LocalDateTime createdAt;

    public History(Application application) {
        this.application = application;
        this.status = application.getStatus();
        this.title = application.getTitle();
        this.content = application.getContent();
        this.reason = application.getReason();
        this.uuid = application.getUuid();
        this.createdAt = application.getCreatedAt();
    }
}
