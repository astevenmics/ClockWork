package mist.mystralix.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DBManager
 * ----------
 * Centralized database connection manager using a HikariCP connection pool.
 *
 * <p>This class is responsible for:
 * <ul>
 *     <li>Building and configuring the connection pool (once on startup)</li>
 *     <li>Providing pooled {@link Connection} instances to repositories</li>
 *     <li>Managing optimal MySQL driver settings for high-throughput workloads</li>
 * </ul>
 *
 * <p>All database access in the application goes through this manager.</p>
 */
public class DBManager {

    /** Shared HikariCP datasource instance (thread-safe, global connection pool). */
    private static HikariDataSource dataSource;

    /**
     * Initializes and builds the HikariCP connection pool.
     *
     * <p>Should be executed once during application startup (e.g., inside
     * ClockWorkContainer). After initialization, all repositories call
     * {@link #getConnection()} to retrieve a pooled database connection.</p>
     *
     * <p>This configuration includes several MySQL-optimized performance flags
     * such as prepared-statement caching, server-side prepared statements,
     * metadata caching, and batch rewrite operations.</p>
     */
    public static void buildConnectionPool() {

        HikariConfig config = new HikariConfig();

        // Load environment variables (recommended: never hardcode credentials)
        String dbPort = System.getenv("DB_PORT");
        config.setJdbcUrl("jdbc:mysql://localhost:" + dbPort + "/clockwork");
        config.setUsername("root");
        config.setPassword(System.getenv("DB_PASSWORD"));

        /*
         * --- PERFORMANCE CONFIGURATIONS ---
         *
         * These properties enhance MySQL performance by reducing parsing,
         * caching metadata, utilizing server-side prepared statements, and
         * rewriting batch inserts for faster throughput.
         */
        config.addDataSourceProperty("cachePrepStmts", "true");           // Cache prepared statements
        config.addDataSourceProperty("prepStmtCacheSize", "250");          // Max cached statements
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");     // Max SQL length cacheable
        config.addDataSourceProperty("useServerPrepStmts", "true");        // MySQL server-side statements
        config.addDataSourceProperty("useLocalSessionState", "true");      // Avoid round-trips on session state
        config.addDataSourceProperty("rewriteBatchedStatements", "true");  // Enables batching optimizations
        config.addDataSourceProperty("cacheResultSetMetadata", "true");    // Cache column metadata
        config.addDataSourceProperty("cacheServerConfiguration", "true");  // Cache config info
        config.addDataSourceProperty("elideSetAutoCommits", "true");       // Reduce auto-commit overhead
        config.addDataSourceProperty("maintainTimeStats", "false");        // Faster performance, no tracking

        /*
         * --- POOL SIZE SETTINGS ---
         *
         * maxPoolSize:    Max concurrent connections managed by Hikari.
         * minIdle:        Minimum idle connections kept for quick retrieval.
         * idleTimeout:    Remove idle connections after 30 seconds.
         * maxLifetime:    Destroy connections after 30 min to avoid server-side timeouts.
         */
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(1800000);

        // Naming the pool helps with debugging in logs + monitoring tools
        config.setPoolName("ClockWork MySQL Pool");

        // Detect connections stuck for more than 2 seconds (debug tool)
        config.setLeakDetectionThreshold(2000);

        // Finally, create the actual pooled DataSource
        dataSource = new HikariDataSource(config);
    }

    /**
     * Retrieves a pooled {@link Connection} from HikariCP.
     *
     * <p>Important: connections MUST be closed after use to return them back
     * to the pool. Always use try-with-resources:</p>
     *
     * <pre>
     * try (Connection conn = DBManager.getConnection()) {
     *     // Work with DB
     * }
     * </pre>
     *
     * @return a pooled connection from the Hikari datasource
     * @throws SQLException if the pool cannot provide a connection
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
