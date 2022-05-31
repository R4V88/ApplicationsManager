package com.applicationsmanager.applicationsmanager.applications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Application {
    @Id
    @GeneratedValue
    Long id;

    String title;

    String content;

    @Enumerated(EnumType.STRING)
    Status status;

    String reason;

    @Version
    Long version;

    Long uuid;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;

    public Application(String title, String content) {
        this.title = title;
        this.content = content;
        this.status = Status.CREATED;
    }

    public UpdateStatusResult updateStatus(Status newStatus) {
        UpdateStatusResult result = this.status.updateStatus(newStatus);
        this.status = result.getStatus();
        return result;
    }
}
