package com.applicationsmanager.applicationsmanager.applications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    Application application;
    //changed to:

    Long applicationId;

    String title;

    String content;

    String reason;

    Long uuid;

    @Enumerated(EnumType.STRING)
    Status status;

    LocalDateTime createdAt;

    public History(Application application) {
        this.applicationId = application.getId();
        this.status = application.getStatus();
        this.title = application.getTitle();
        this.content = application.getContent();
        this.reason = application.getReason();
        this.uuid = application.getUuid();
        this.createdAt = application.getUpdatedAt();
    }
}
