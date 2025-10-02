package hexlet.code.app.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
public final class Label {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank @Size(min = 3, max = 1000) @Column(columnDefinition = "TEXT", unique = true)
    private String name;

    @CreatedDate
    private LocalDateTime createdAt;
}
