package hexlet.code.extension;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public final class PostgresDbCleanerExtension implements BeforeEachCallback {
    private final List<String> tables = List.of("TASKS_LABELS", "TASKS", "LABELS", "TASK_STATUSES", "USERS");

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ApplicationContext appCtx = SpringExtension.getApplicationContext(context);
        DataSource dataSource = appCtx.getBean(DataSource.class);
        cleanDatabase(dataSource);
    }

    private void cleanDatabase(DataSource dataSource) throws SQLException {

        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            for (String table : tables) {
                String sql = "DELETE FROM \"" + table + "\"";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }

            connection.commit();
        }
    }
}
