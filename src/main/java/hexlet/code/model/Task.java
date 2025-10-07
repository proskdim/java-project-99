package hexlet.code.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tasks")
@Getter
@Setter
public final class Task implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank private String name;

    private Integer index;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull @ManyToOne
    private TaskStatus taskStatus;

    @ManyToOne
    private User assignee;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tasks_labels", joinColumns = @JoinColumn(name = "task_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "label_id", nullable = false))
    private Set<Label> labels = new HashSet<>();
}
