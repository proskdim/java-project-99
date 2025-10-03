package hexlet.code.app.specification;

import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public final class TaskSpecification {

    private static Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query,
                cb) -> assigneeId == null ? cb.conjunction() : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private static Specification<Task> withLabelIdId(Long labelId) {
        return (root, query,
                cb) -> labelId == null ? cb.conjunction() : cb.equal(root.get("labels").get("id"), labelId);
    }

    private static Specification<Task> withStatus(String status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("taskStatus").get("slug")), "%" + status.toLowerCase() + "%");
    }

    private static Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
    }

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont()).and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus())).and(withLabelIdId(params.getLabelId()));
    }
}