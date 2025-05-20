package cz.cvut.fel.dbs;

import cz.cvut.fel.dbs.entity.*;
import cz.cvut.fel.dbs.service.*;
import cz.cvut.fel.dbs.service.impl.*;

import jakarta.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class DemoController {
	private final UserService userService = new UserServiceImpl();
	private final AlbumService albumService = new AlbumServiceImpl();
	private final SongService songService = new SongServiceImpl();
	private final PlaylistService playlistService = new PlaylistServiceImpl();

	// Store created entities for later use
	private RegularUser createdRegularUser;
	private Artist foundArtist;
	private Album createdAlbum;
	private Playlist createdPlaylist;

	// Date formatter for better readability
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Runs all demo use cases in sequence with pauses between them
	 */
	public void runAllUseCases() {
		System.out.println("Running all use cases sequentially with pauses between them.");
		System.out.println("Press Enter after each use case to continue to the next one.");

		pauseForUserInput();
		registerNewUser();

		pauseForUserInput();
		createAlbumWithSongs();

		pauseForUserInput();
		createPlaylistWithSongs();

		pauseForUserInput();
		followUser();

		pauseForUserInput();
		saveAlbum();

		System.out.println("\nAll use cases completed successfully!");
	}

	/**
	 * Pauses execution and waits for user to press Enter
	 */
	private void pauseForUserInput() {
		System.out.println("\nPress Enter to continue...");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}

	/**
	 * Use Case 1: Register a new regular user
	 */
	public void registerNewUser() {
		printUseCaseHeader(1, "REGISTER A NEW REGULAR USER");

		// Generate a unique username
		String username = "user_" + System.currentTimeMillis();
		String password = "password123";
		String firstName = "John";
		String lastName = "Doe";
		String email = username + "@example.com";
		String subscriptionType = "Premium";
		String paymentMethod = "Credit Card";
		String renewalDate = "2025-12-31";

		System.out.println("Creating new regular user with the following details:");
		System.out.println("  Username: " + username);
		System.out.println("  Password: " + password);
		System.out.println("  Name: " + firstName + " " + lastName);
		System.out.println("  Email: " + email);
		System.out.println("  Subscription: " + subscriptionType);
		System.out.println("  Payment Method: " + paymentMethod);
		System.out.println("  Renewal Date: " + renewalDate);

		try {
			System.out.println("\nCalling userService.registerRegularUser()...");
			RegularUser user = userService.registerRegularUser(
				username, password, firstName, lastName, email,
				subscriptionType, paymentMethod, renewalDate
			);

			this.createdRegularUser = user;

			System.out.println("✅ User registered successfully!");
			System.out.println("User details from database:");
			System.out.println("  Username: " + user.getUsername());
			System.out.println("  Full name: " + user.getFirstName() + " " + user.getLastName());
			System.out.println("  Email: " + user.getEmail());
			System.out.println("  Registration Date: " + dateFormat.format(user.getRegistrationDate()));
			System.out.println("  Subscription: " + user.getSubscriptionType());
			System.out.println("  Payment Method: " + user.getPaymentMethod());
			System.out.println("  Renewal Date: " + user.getRenewalDate());

			printUseCaseFooter(1, true);
		} catch (Exception e) {
			System.out.println("❌ Failed to register user: " + e.getMessage());
			printUseCaseFooter(1, false);
			throw e;
		}
	}

	/**
	 * Use Case 2: Artist adding a new album with songs
	 */
	public void createAlbumWithSongs() {
		printUseCaseHeader(2, "ARTIST ADDING A NEW ALBUM WITH SONGS");

		try {
			// First, find an artist in the database
			System.out.println("Finding an existing artist in the database...");
			List<User> users = userService.findUsersByName("a");

			foundArtist = null;
			for (User user : users) {
				if (user instanceof Artist) {
					foundArtist = (Artist) user;
					break;
				}
			}

			if (foundArtist == null) {
				System.out.println("⚠️ No artists found in database. Creating a dummy artist...");

				// If no artist exists, try to create one
				String artistUsername = "artist_" + System.currentTimeMillis();
				Artist newArtist = createDummyArtist(artistUsername);
				if (newArtist != null) {
					foundArtist = newArtist;
				} else {
					throw new RuntimeException("Failed to create a dummy artist");
				}
			}

			System.out.println("\nFound artist:");
			System.out.println("  Username: " + foundArtist.getUsername());
			System.out.println("  Stage Name: " + foundArtist.getStageName());
			System.out.println("  Biography: " + foundArtist.getBiography());

			// Create a new album
			String albumTitle = "Album_" + System.currentTimeMillis();
			Date releaseDate = new Date(); // Today

			System.out.println("\nCreating new album:");
			System.out.println("  Title: " + albumTitle);
			System.out.println("  Artist: " + foundArtist.getStageName());
			System.out.println("  Release Date: " + dateFormat.format(releaseDate));

			System.out.println("\nCalling albumService.createAlbum()...");
			Album album = albumService.createAlbum(foundArtist.getUsername(), albumTitle, releaseDate);
			this.createdAlbum = album;

			System.out.println("✅ Album created successfully!");
			System.out.println("Album details from database:");
			System.out.println("  Title: " + album.getTitle());
			System.out.println("  Artist: " + album.getPrimaryArtist().getStageName());
			System.out.println("  Release Date: " + dateFormat.format(album.getReleaseDate()));
			System.out.println("  Initial Duration: " + formatDuration(album.getTotalDuration()));
			System.out.println("  Raw Duration (seconds): " + album.getTotalDuration());

			// Add genre to the album
			String genreName = "Pop";
			System.out.println("\nAdding genre '" + genreName + "' to album...");
			albumService.addGenreToAlbum(album.getTitle(), 
				foundArtist.getUsername(), 
				releaseDate, 
				genreName);
			System.out.println("✅ Genre added to album successfully!");

			// Add songs to the album
			System.out.println("\nAdding 3 songs to the album...");
			for (int i = 1; i <= 3; i++) {
				String songTitle = "Track " + i;
				String filePath = "/music/" + foundArtist.getUsername() + "/" + albumTitle + "/" + songTitle + ".mp3";
				long duration = 180 + (i * 30); // 3 minutes + 30 seconds for each track
				String lyrics = "Lyrics for " + songTitle;

				System.out.println("\nSong " + i + " details:");
				System.out.println("  Title: " + songTitle);
				System.out.println("  File Path: " + filePath);
				System.out.println("  Duration: " + formatDuration(duration));
				System.out.println("  Raw Duration (seconds): " + duration);
				System.out.println("  Track Number: " + i);

				System.out.println("Calling songService.addSongToAlbum()...");
				Song song = songService.addSongToAlbum(
					songTitle, albumTitle, foundArtist.getUsername(), releaseDate,
					filePath, duration, lyrics, i
				);

				System.out.println("✅ Song added successfully!");
				System.out.println("Song details from database:");
				System.out.println("  Title: " + song.getTitle());
				System.out.println("  Album: " + song.getAlbum().getTitle());
				System.out.println("  Duration: " + formatDuration(song.getDuration()));
				System.out.println("  Raw Duration (seconds): " + song.getDuration());
				System.out.println("  Track Number: " + song.getTrackNumber());

				// Add genre to the song
				System.out.println("Adding genre '" + genreName + "' to song...");
				songService.addGenreToSong(
					songTitle, albumTitle, foundArtist.getUsername(), releaseDate, genreName
				);
				System.out.println("✅ Genre added to song successfully!");
			}

			// Verify songs were added and album duration was updated
			System.out.println("\nVerifying songs in the album...");
			List<Song> songs = songService.findSongsByAlbum(albumTitle, foundArtist.getUsername(), releaseDate);
			System.out.println("Found " + songs.size() + " songs in the album");

			// Calculate total duration of all songs
			long calculatedTotalDuration = 0;
			for (Song song : songs) {
				calculatedTotalDuration += song.getDuration();
				System.out.println("  Song: " + song.getTitle() + " - Duration: " + 
					formatDuration(song.getDuration()) + " (" + song.getDuration() + " seconds)");
			}
			System.out.println("Calculated total duration: " + formatDuration(calculatedTotalDuration) + 
				" (" + calculatedTotalDuration + " seconds)");

			// Get updated album with fresh data to see total duration
			Optional<Album> updatedAlbumOpt = albumService.findAlbum(albumTitle, foundArtist.getUsername(), releaseDate);
			if (updatedAlbumOpt.isPresent()) {
				Album updatedAlbum = updatedAlbumOpt.get();
				System.out.println("Updated album duration: " + formatDuration(updatedAlbum.getTotalDuration()) + 
					" (" + updatedAlbum.getTotalDuration() + " seconds)");

				// Check if the total duration matches what we calculated
				if (updatedAlbum.getTotalDuration() != calculatedTotalDuration) {
					System.out.println("⚠️ Warning: Album duration doesn't match the sum of song durations!");

					// Fix album duration if incorrect
					if (updatedAlbum.getTotalDuration() == 0 || 
					updatedAlbum.getTotalDuration() != calculatedTotalDuration) {

						System.out.println("Attempting to fix album duration...");
						try {
							// Create a new transaction to update the album
							EntityManager em = DatabaseManager.getEntityManager();
							em.getTransaction().begin();
							Album albumToFix = em.find(Album.class, 
								new Album.AlbumId(albumTitle, foundArtist.getUsername(), releaseDate));
							if (albumToFix != null) {
								albumToFix.setTotalDuration(calculatedTotalDuration);
								em.flush();
								em.refresh(albumToFix);
								System.out.println("Fixed album duration to: " + 
									formatDuration(albumToFix.getTotalDuration()) + 
									" (" + albumToFix.getTotalDuration() + " seconds)");
							}
							em.getTransaction().commit();
						} catch (Exception e) {
							System.out.println("Failed to fix album duration: " + e.getMessage());
						}
					}
				}
			}

			printUseCaseFooter(2, true);
		} catch (Exception e) {
			System.out.println("❌ Failed to create album with songs: " + e.getMessage());
			printUseCaseFooter(2, false);
			throw e;
		}
	}

	/**
	 * Use Case 3: User creating a playlist and adding songs to it
	 */
	public void createPlaylistWithSongs() {
		printUseCaseHeader(3, "USER CREATING A PLAYLIST AND ADDING SONGS");

		try {
			// Use the regular user created in use case 1, or find another one
			User user = createdRegularUser;
			if (user == null) {
				System.out.println("No user from previous use case. Finding a regular user...");
				List<User> users = userService.findUsersByName("a");
				user = users.stream()
				.filter(u -> !(u instanceof Artist))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No regular users found"));
			}

			System.out.println("Using user:");
			System.out.println("  Username: " + user.getUsername());
			System.out.println("  Name: " + user.getFirstName() + " " + user.getLastName());

			// Create a new playlist
			String playlistTitle = "Playlist_" + System.currentTimeMillis();
			String description = "A collection of my favorite songs";
			boolean isPublic = true;

			System.out.println("\nCreating new playlist:");
			System.out.println("  Title: " + playlistTitle);
			System.out.println("  Creator: " + user.getUsername());
			System.out.println("  Description: " + description);
			System.out.println("  Public: " + isPublic);

			System.out.println("\nCalling playlistService.createPlaylist()...");
			Playlist playlist = playlistService.createPlaylist(
				user.getUsername(), playlistTitle, description, isPublic
			);
			this.createdPlaylist = playlist;

			System.out.println("✅ Playlist created successfully!");
			System.out.println("Playlist details from database:");
			System.out.println("  Title: " + playlist.getTitle());
			System.out.println("  Creator: " + playlist.getCreator().getUsername());
			System.out.println("  Creation Date: " + dateFormat.format(playlist.getCreationDate()));
			System.out.println("  Description: " + playlist.getDescription());
			System.out.println("  Public: " + playlist.isPublic());

			// Find songs to add to the playlist
			System.out.println("\nFinding songs with genre 'Pop' to add to the playlist...");
			List<Song> songs = songService.findSongsByGenre("Pop");

			if (songs.isEmpty()) {
				// If no songs with 'Pop' genre, try to find any songs
				System.out.println("No songs with 'Pop' genre found. Finding any songs...");
				// Use the created album from use case 2, if available
				if (createdAlbum != null && foundArtist != null) {
					songs = songService.findSongsByAlbum(
						createdAlbum.getTitle(), 
						foundArtist.getUsername(),
						createdAlbum.getReleaseDate()
					);
				}

				// If still no songs, throw an error
				if (songs.isEmpty()) {
					throw new RuntimeException("No songs found in the database");
				}
			}

			System.out.println("Found " + songs.size() + " songs to add to the playlist");

			// Add songs to the playlist
			System.out.println("\nAdding songs to the playlist...");
			int addedCount = 0;
			for (int i = 0; i < Math.min(3, songs.size()); i++) {
				Song song = songs.get(i);

				System.out.println("\nAdding song " + (i+1) + ":");
				System.out.println("  Title: " + song.getTitle());
				System.out.println("  Album: " + song.getAlbum().getTitle());
				System.out.println("  Artist: " + song.getAlbum().getPrimaryArtist().getStageName());

				System.out.println("Calling playlistService.addSongToPlaylist()...");
				boolean added = playlistService.addSongToPlaylist(
					playlistTitle, user.getUsername(),
					song.getTitle(), song.getId().getAlbumTitle(),
					song.getId().getPrimaryAlbumArtistUsername(), song.getId().getAlbumReleaseDate()
				);

				if (added) {
					System.out.println("✅ Song added to playlist successfully!");
					addedCount++;
				} else {
					System.out.println("⚠️ Failed to add song to playlist");
				}
			}

			// Verify songs were added
			System.out.println("\nVerifying songs in the playlist...");
			List<Song> playlistSongs = playlistService.getSongsInPlaylist(playlistTitle, user.getUsername());
			System.out.println("Found " + playlistSongs.size() + " songs in the playlist");

			if (playlistSongs.size() > 0) {
				System.out.println("\nSongs in playlist:");
				for (int i = 0; i < playlistSongs.size(); i++) {
					Song song = playlistSongs.get(i);
					System.out.println((i+1) + ". " + song.getTitle() + " - " + 
						song.getAlbum().getPrimaryArtist().getStageName() + 
						" (" + formatDuration(song.getDuration()) + ")");
				}
			}

			printUseCaseFooter(3, true);
		} catch (Exception e) {
			System.out.println("❌ Failed to create playlist and add songs: " + e.getMessage());
			printUseCaseFooter(3, false);
			throw e;
		}
	}

	/**
	 * Use Case 4: User following another user
	 */
	public void followUser() {
		printUseCaseHeader(4, "USER FOLLOWING ANOTHER USER");

		try {
			// Get two users from the database
			System.out.println("Finding two different users in the database...");
			List<User> users = userService.findUsersByName("a");
			if (users.size() < 2) {
				System.out.println("Need at least 2 users in the database. Using created user if available...");

				if (createdRegularUser != null) {
					users.add(createdRegularUser);
				}

				if (foundArtist != null) {
					users.add(foundArtist);
				}

				if (users.size() < 2) {
					throw new RuntimeException("Need at least 2 users in the database");
				}
			}

			User follower = users.get(0);
			User following = users.get(1);

			// Make sure they're different users
			if (follower.getUsername().equals(following.getUsername())) {
				for (int i = 2; i < users.size(); i++) {
					User potentialFollowing = users.get(i);
					if (!potentialFollowing.getUsername().equals(follower.getUsername())) {
						following = potentialFollowing;
						break;
					}
				}

				if (follower.getUsername().equals(following.getUsername())) {
					throw new RuntimeException("Couldn't find two different users");
				}
			}

			System.out.println("\nFollower:");
			System.out.println("  Username: " + follower.getUsername());
			System.out.println("  Name: " + follower.getFirstName() + " " + follower.getLastName());

			System.out.println("\nFollowing:");
			System.out.println("  Username: " + following.getUsername());
			System.out.println("  Name: " + following.getFirstName() + " " + following.getLastName());

			// Create the follow relationship
			System.out.println("\nCalling userService.followUser()...");
			boolean success = userService.followUser(follower.getUsername(), following.getUsername());

			if (success) {
				System.out.println("✅ Follow relationship created successfully!");

				// Get followers and following
				List<User> followers = userService.getFollowers(following.getUsername());
				List<User> followingList = userService.getFollowing(follower.getUsername());

				System.out.println("\nVerifying follow relationship:");
				System.out.println(following.getUsername() + " has " + followers.size() + " followers");
				System.out.println(follower.getUsername() + " is following " + followingList.size() + " users");

				// Display followers
				if (followers.size() > 0) {
					System.out.println("\nUsers following " + following.getUsername() + ":");
					for (int i = 0; i < followers.size(); i++) {
						User followerUser = followers.get(i);
						System.out.println((i+1) + ". " + followerUser.getUsername() + 
							" (" + followerUser.getFirstName() + " " + 
							followerUser.getLastName() + ")");
					}
				}

				// Display following
				if (followingList.size() > 0) {
					System.out.println("\nUsers that " + follower.getUsername() + " is following:");
					for (int i = 0; i < followingList.size(); i++) {
						User followingUser = followingList.get(i);
						System.out.println((i+1) + ". " + followingUser.getUsername() + 
							" (" + followingUser.getFirstName() + " " + 
							followingUser.getLastName() + ")");
					}
				}
			} else {
				System.out.println("⚠️ Failed to create follow relationship");
			}

			printUseCaseFooter(4, true);
		} catch (Exception e) {
			System.out.println("❌ Failed to create follow relationship: " + e.getMessage());
			printUseCaseFooter(4, false);
			throw e;
		}
	}

	/**
	 * Use Case 5: User saving an album
	 */
	public void saveAlbum() {
		printUseCaseHeader(5, "USER SAVING AN ALBUM");

		try {
			// Use the regular user created in use case 1, or find another one
			User user = createdRegularUser;
			if (user == null) {
				System.out.println("No user from previous use case. Finding a regular user...");
				List<User> users = userService.findUsersByName("a");
				user = users.stream()
				.filter(u -> !(u instanceof Artist))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No regular users found"));
			}

			System.out.println("Using user:");
			System.out.println("  Username: " + user.getUsername());
			System.out.println("  Name: " + user.getFirstName() + " " + user.getLastName());

			// Use the album created in use case 2, or find another one
			Album album = createdAlbum;
			Artist artist = foundArtist;

			if (album == null || artist == null) {
				System.out.println("No album from previous use case. Finding an album...");
				List<Artist> artists = userService.findUsersByName("a").stream()
				.filter(u -> u instanceof Artist)
				.map(u -> (Artist) u)
				.toList();

				if (artists.isEmpty()) {
					throw new RuntimeException("No artists found");
				}

				artist = artists.get(0);
				List<Album> albums = albumService.findAlbumsByArtist(artist.getUsername());

				if (albums.isEmpty()) {
					throw new RuntimeException("No albums found for artist: " + artist.getUsername());
				}

				album = albums.get(0);
			}

			System.out.println("\nUsing album:");
			System.out.println("  Title: " + album.getTitle());
			System.out.println("  Artist: " + artist.getStageName());
			System.out.println("  Release Date: " + dateFormat.format(album.getReleaseDate()));

			// Save the album for the user
			System.out.println("\nCalling albumService.saveAlbum()...");
			boolean saved = albumService.saveAlbum(
				user.getUsername(), album.getTitle(), artist.getUsername(), album.getReleaseDate()
			);

			if (saved) {
				System.out.println("✅ Album saved successfully!");
				System.out.println("User " + user.getUsername() + " has saved album \"" + 
					album.getTitle() + "\" by " + artist.getStageName());
			} else {
				System.out.println("⚠️ Failed to save album");
			}

			printUseCaseFooter(5, true);
		} catch (Exception e) {
			System.out.println("❌ Failed to save album: " + e.getMessage());
			printUseCaseFooter(5, false);
			throw e;
		}
	}

	// Helper methods

	/**
	 * Format seconds into mm:ss format
	 */
	private String formatDuration(long seconds) {
		long minutes = seconds / 60;
		long remainingSeconds = seconds % 60;
		return String.format("%d:%02d", minutes, remainingSeconds);
	}

	/**
	 * Print a nicely formatted header for a use case
	 */
	private void printUseCaseHeader(int useCaseNumber, String description) {
		System.out.println("\n");
		System.out.println("╔════════════════════════════════════════════════════════════╗");
		System.out.println("║  USE CASE " + useCaseNumber + ": " + description + 
			" ".repeat(Math.max(0, 50 - description.length())));
		System.out.println("╚════════════════════════════════════════════════════════════╝");
	}

	/**
	 * Print a nicely formatted footer for a use case
	 */
	private void printUseCaseFooter(int useCaseNumber, boolean success) {
		String status = success ? "COMPLETED SUCCESSFULLY ✅" : "FAILED ❌";
		System.out.println("\n");
		System.out.println("╔════════════════════════════════════════════════════════════╗");
		System.out.println("║  USE CASE " + useCaseNumber + " " + status + 
			" ".repeat(Math.max(0, 50 - status.length() - 11)));
		System.out.println("╚════════════════════════════════════════════════════════════╝");
		System.out.println("\n");
	}

	/**
	 * Create a dummy artist if none is found in the database
	 */
	private Artist createDummyArtist(String username) {
		try {
			System.out.println("Creating a dummy artist with username: " + username);

			// We need to manually insert an artist since it's not in our service methods
			jakarta.persistence.EntityManager em = DatabaseManager.getEntityManager();
			try {
				em.getTransaction().begin();

				Artist artist = new Artist(
					username, "password", "Test", "Artist",
					username + "@example.com", new Date(), "Test Artist", "A test artist biography"
				);

				em.persist(artist);
				em.getTransaction().commit();

				System.out.println("✅ Created dummy artist successfully");
				return artist;
			} catch (Exception e) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				System.out.println("❌ Failed to create dummy artist: " + e.getMessage());
				return null;
			}
		} catch (Exception e) {
			System.out.println("❌ Failed to create dummy artist: " + e.getMessage());
			return null;
		}
	}
}
