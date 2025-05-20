package cz.cvut.fel.dbs.service;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
	private static final String PERSISTENCE_UNIT_NAME = "MusicStreamingPU";
	private static EntityManagerFactory emf;
	private static Dotenv dotenv;

	public static void init() {
		if (emf == null) {
			try {
				// Load environment variables from .env file
				dotenv = Dotenv.configure().ignoreIfMissing().load();

				// Create properties map for entity manager factory
				Map<String, String> properties = new HashMap<>();

				// Get database credentials from .env file or use defaults
				String dbUrl = getEnvOrDefault("DB_URL", 
					"jdbc:postgresql://slon.felk.cvut.cz:5432/teterheo");
				String dbUser = getEnvOrDefault("DB_USER", "teterheo");
				String dbPass = getEnvOrDefault("DB_PASSWORD", "");

				System.out.println("Connecting to database: " + dbUrl);
				System.out.println("With user: " + dbUser);

				// Set database connection properties
				properties.put("jakarta.persistence.jdbc.url", dbUrl);
				properties.put("jakarta.persistence.jdbc.user", dbUser);
				properties.put("jakarta.persistence.jdbc.password", dbPass);

				// REMOVED: Cache configuration that was causing problems
				// Only keep the basic JDBC batch size for performance
				properties.put("hibernate.jdbc.batch_size", "50");

				// Create entity manager factory with properties
				emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

				System.out.println("EntityManagerFactory created successfully");
			} catch (Exception e) {
				System.err.println("Error initializing EntityManagerFactory: " + e.getMessage());
				throw e;
			}
		}
	}

	public static EntityManager getEntityManager() {
		if (emf == null) {
			init();
		}
		return emf.createEntityManager();
	}

	public static void close() {
		if (emf != null && emf.isOpen()) {
			emf.close();
			System.out.println("EntityManagerFactory closed");
		}
	}

	/**
	 * Get environment variable or default value if not found
	 */
	private static String getEnvOrDefault(String key, String defaultValue) {
		if (dotenv == null) {
			return defaultValue;
		}

		String value = dotenv.get(key);
		return (value != null) ? value : defaultValue;
	}
}
