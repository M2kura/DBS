package cz.cvut.fel.dbs.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseManager {
	private static final String PERSISTENCE_UNIT_NAME = "MusicStreamingPU";
	private static EntityManagerFactory emf;

	public static void init() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
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
		}
	}
}
