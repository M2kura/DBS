package cz.cvut.fel.dbs;

import cz.cvut.fel.dbs.service.DatabaseManager;

public class Main {
	public static void main(String[] args) {
		try {
			System.out.println("\n========== MUSIC STREAMING SERVICE DEMO ==========\n");

			DemoController demo = new DemoController();

			demo.runAllUseCases();

			// Or run individual use cases as needed
			// demo.registerNewUser();
			// demo.createAlbumWithSongs();
			// demo.createPlaylistWithSongs(); 
			// demo.followUser();
			// demo.saveAlbum();

			System.out.println("\n========== DEMO COMPLETED SUCCESSFULLY ==========\n");
		} catch (Exception e) {
			System.err.println("\n========== ERROR DURING DEMO ==========");
			System.err.println("Error message: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Close database connection
			DatabaseManager.close();
		}
	}
}
