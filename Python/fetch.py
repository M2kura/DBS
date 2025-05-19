import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
import psycopg2
import datetime
import random
import os
import time
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Spotify API credentials
CLIENT_ID = os.getenv('SPOTIFY_CLIENT_ID')
CLIENT_SECRET = os.getenv('SPOTIFY_CLIENT_SECRET')

# PostgreSQL connection parameters
DB_HOST = os.getenv('DB_HOST', 'slon.felk.cvut.cz')
DB_NAME = os.getenv('DB_NAME')  # Your database name
DB_USER = os.getenv('DB_USER')  # Your username
DB_PASS = os.getenv('DB_PASS')  # Your password

# Initialize Spotify client
sp = spotipy.Spotify(auth_manager=SpotifyClientCredentials(
    client_id=CLIENT_ID,
    client_secret=CLIENT_SECRET
))

# Connect to PostgreSQL
conn = psycopg2.connect(
    host=DB_HOST,
    database=DB_NAME,
    user=DB_USER,
    password=DB_PASS
)
cur = conn.cursor()

def clean_database():
    """
    Cleans all existing data from the database to start fresh.
    Deletes in reverse order of foreign key dependencies.
    """
    print("Cleaning database - removing all existing data...")
    
    try:
        # Turn off foreign key constraints temporarily for faster deletion
        cur.execute("SET CONSTRAINTS ALL DEFERRED;")
        
        # Delete from most dependent tables first
        tables = [
            "Includes",
            "Saves_Playlist",
            "Saves_Album",
            "Follows",
            "Song_Genre",
            "Album_Genre",
            "Song_Artist",
            "Album_Artist", 
            "Song",
            "Album",
            "Playlist",
            "Artist",
            "Regular_User",
            "Profile_Page",
            "User",
            "Genre"
        ]
        
        for table in tables:
            try:
                print(f"Deleting all records from {table}...")
                cur.execute(f'DELETE FROM "{table}";')
            except Exception as e:
                print(f"Error deleting from {table}: {e}")
                # Continue with other tables even if one fails
        
        # Re-enable foreign key constraints
        conn.commit()
        print("Database cleaned successfully")
        
    except Exception as e:
        print(f"Error cleaning database: {e}")
        conn.rollback()
        
    # Reset sequences if any tables have serial columns
    # Since your schema doesn't seem to use serial columns, this might not be needed
    # But including it just in case
    try:
        cur.execute("SELECT relname FROM pg_class WHERE relkind = 'S';")
        sequences = cur.fetchall()
        for seq in sequences:
            seq_name = seq[0]
            print(f"Resetting sequence {seq_name}...")
            cur.execute(f"ALTER SEQUENCE {seq_name} RESTART WITH 1;")
        conn.commit()
    except Exception as e:
        print(f"Error resetting sequences: {e}")
        # This is not critical, so we can continue even if it fails

# Helper functions
def sanitize_string(text):
    """Sanitize strings for SQL insertion"""
    if text is None:
        return ""
    # Replace single quotes with two single quotes to escape properly
    # This is the PostgreSQL way of escaping single quotes
    sanitized = text.replace("'", "''").replace("\n", " ")
    # Limit length to 255 characters to fit varchar(255)
    return sanitized[:255]

def generate_random_date(start_year=2010, end_year=2023):
    """Generate a random date between start_year and end_year"""
    start_date = datetime.date(start_year, 1, 1)
    end_date = datetime.date(end_year, 12, 31)
    days_between = (end_date - start_date).days
    random_days = random.randint(0, days_between)
    return start_date + datetime.timedelta(days=random_days)

def generate_password_hash():
    """Generate a dummy password hash"""
    return "hashed_password_" + str(random.randint(10000, 99999))

def insert_user(username, first_name, last_name, email):
    """Insert a user into the User table"""
    try:
        registration_date = generate_random_date(2018, 2023)
        password = generate_password_hash()
        
        query = """
        INSERT INTO "User" ("Username", "Password", "First_Name", "Last_Name", "Email", "Registration_Date")
        VALUES (%s, %s, %s, %s, %s, %s)
        ON CONFLICT ("Username") DO NOTHING;
        """
        cur.execute(query, (username, password, first_name, last_name, email, registration_date))
        
        # Insert profile page
        url = f"musicservice.com/profile/{username}"
        is_public = random.choice([True, False])
        query = """
        INSERT INTO "Profile_Page" ("URL", "Username", "Is_Public")
        VALUES (%s, %s, %s)
        ON CONFLICT ("URL") DO NOTHING;
        """
        cur.execute(query, (url, username, is_public))
        
        return True
    except Exception as e:
        print(f"Error inserting user {username}: {e}")
        conn.rollback()
        return False

def insert_artist(username, stage_name, biography):
    """Insert an artist into the Artist table"""
    try:
        query = """
        INSERT INTO "Artist" ("Username", "Stage_Name", "Biography")
        VALUES (%s, %s, %s)
        ON CONFLICT ("Username") DO NOTHING;
        """
        cur.execute(query, (username, stage_name, biography))
        return True
    except Exception as e:
        print(f"Error inserting artist {username}: {e}")
        conn.rollback()
        return False

def insert_album(title, artist_username, release_date, total_duration=0):
    """Insert an album into the Album table"""
    try:
        query = """
        INSERT INTO "Album" ("Title", "Primary_Artist_Username", "Release_Date", "Total_Duration")
        VALUES (%s, %s, %s, %s)
        ON CONFLICT ("Title", "Primary_Artist_Username", "Release_Date") DO NOTHING;
        """
        cur.execute(query, (title, artist_username, release_date, total_duration))
        return True
    except Exception as e:
        print(f"Error inserting album {title}: {e}")
        conn.rollback()
        return False

def insert_song(title, album_title, artist_username, album_release_date, 
                file_path, duration, lyrics="", track_number=1):
    """Insert a song into the Song table"""
    try:
        query = """
        INSERT INTO "Song" ("Title", "Album_Title", "Primary_Album_Artist_Username", 
                         "Album_Release_Date", "File_Path", "Duration", "Lyrics", "Track_Number")
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT ("Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") DO NOTHING;
        """
        cur.execute(query, (title, album_title, artist_username, album_release_date, 
                          file_path, duration, lyrics, track_number))
        return True
    except Exception as e:
        print(f"Error inserting song {title}: {e}")
        conn.rollback()
        return False

def insert_genre(genre_name):
    """Insert a genre into the Genre table"""
    try:
        query = """
        INSERT INTO "Genre" ("Name")
        VALUES (%s)
        ON CONFLICT ("Name") DO NOTHING;
        """
        cur.execute(query, (genre_name,))
        return True
    except Exception as e:
        print(f"Error inserting genre {genre_name}: {e}")
        conn.rollback()
        return False

def insert_album_genre(album_title, artist_username, release_date, genre_name):
    """Insert an album genre association"""
    try:
        query = """
        INSERT INTO "Album_Genre" ("Album_Title", "Primary_Artist_Username", "Release_Date", "Genre_Name")
        VALUES (%s, %s, %s, %s)
        ON CONFLICT ("Album_Title", "Primary_Artist_Username", "Release_Date", "Genre_Name") DO NOTHING;
        """
        cur.execute(query, (album_title, artist_username, release_date, genre_name))
        return True
    except Exception as e:
        print(f"Error inserting album genre: {e}")
        conn.rollback()
        return False

def insert_song_genre(song_title, album_title, artist_username, album_release_date, genre_name):
    """Insert a song genre association"""
    try:
        query = """
        INSERT INTO "Song_Genre" ("Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date", "Genre_Name")
        VALUES (%s, %s, %s, %s, %s)
        ON CONFLICT ("Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date", "Genre_Name") DO NOTHING;
        """
        cur.execute(query, (song_title, album_title, artist_username, album_release_date, genre_name))
        return True
    except Exception as e:
        print(f"Error inserting song genre: {e}")
        conn.rollback()
        return False

def fetch_artists_and_insert(artists_to_search, limit=10):
    """Fetch artists from Spotify and insert into the database"""
    artist_usernames = []
    
    for artist_name in artists_to_search:
        results = sp.search(q=f'artist:{artist_name}', type='artist', limit=1)
        
        if not results['artists']['items']:
            print(f"No results found for artist: {artist_name}")
            continue
        
        artist = results['artists']['items'][0]
        
        # Create a username based on artist name
        username = artist_name.lower().replace(' ', '_')[:20] + str(random.randint(100, 999))
        
        # Artist details
        stage_name = sanitize_string(artist['name'])
        followers = artist.get('followers', {}).get('total', 0)
        popularity = artist.get('popularity', 0)
        
        # Create a bio with follower and popularity info
        biography = sanitize_string(f"Artist with {followers} followers. Popularity: {popularity}/100.")
        
        # Split name for first and last name
        if " " in artist_name:
            parts = artist_name.split(" ", 1)
            first_name = sanitize_string(parts[0])
            last_name = sanitize_string(parts[1])
        else:
            first_name = sanitize_string(artist_name)
            last_name = "Music"
        
        email = f"{username}@artists.example.com"
        
        # Insert user first, then artist
        if insert_user(username, first_name, last_name, email):
            if insert_artist(username, stage_name, biography):
                artist_usernames.append((username, artist['id']))
                print(f"Added artist: {stage_name} (Username: {username})")
                
                # Insert genres
                for genre in artist.get('genres', [])[:5]:  # Limit to 5 genres
                    genre_name = sanitize_string(genre)
                    insert_genre(genre_name)
    
    return artist_usernames

def fetch_albums_and_songs(artist_usernames, albums_per_artist=2, tracks_per_album=5):
    """Fetch albums and songs for each artist and insert into the database"""
    for username, artist_id in artist_usernames:
        # Get albums
        albums = sp.artist_albums(artist_id, album_type='album', limit=albums_per_artist)
        
        for album in albums['items']:
            album_title = sanitize_string(album['name'])
            release_date = album['release_date']
            
            # If only year is provided, add month and day
            if len(release_date) == 4:
                release_date = f"{release_date}-01-01"
            elif len(release_date) == 7:
                release_date = f"{release_date}-01"
                
            # Get album genres
            album_data = sp.album(album['id'])
            album_genres = album_data.get('genres', [])
            
            # If album has no genres, use artist genres
            if not album_genres:
                artist_data = sp.artist(artist_id)
                album_genres = artist_data.get('genres', [])[:2]  # Limit to 2 genres
            
            # Insert genres
            for genre in album_genres:
                genre_name = sanitize_string(genre)
                insert_genre(genre_name)
            
            # Get tracks
            tracks = sp.album_tracks(album['id'], limit=tracks_per_album)
            total_duration = 0
            
            # Insert album
            if insert_album(album_title, username, release_date):
                print(f"Added album: {album_title} by {username}")
                
                # Insert album genres
                for genre in album_genres:
                    genre_name = sanitize_string(genre)
                    insert_album_genre(album_title, username, release_date, genre_name)
                
                # Insert tracks
                for i, track in enumerate(tracks['items'], 1):
                    track_title = sanitize_string(track['name'])
                    duration_ms = track['duration_ms']
                    duration_sec = duration_ms // 1000  # Convert to seconds
                    total_duration += duration_sec
                    
                    # Create a unique file path
                    file_path = f"/music/{username}/{album_title.replace(' ', '_')}/{i}_{track_title.replace(' ', '_')}.mp3"
                    file_path = sanitize_string(file_path)
                    
                    # Try to get lyrics (dummy for this example)
                    lyrics = "Lyrics not available"
                    
                    # Insert song
                    if insert_song(track_title, album_title, username, release_date, 
                                  file_path, duration_sec, lyrics, i):
                        print(f"  Added track: {track_title}")
                        
                        # Insert song genres (use same genres as album)
                        for genre in album_genres:
                            genre_name = sanitize_string(genre)
                            insert_song_genre(track_title, album_title, username, release_date, genre_name)
                
                # Update album with total duration
                update_query = f"""
                UPDATE "Album" 
                SET "Total_Duration" = {total_duration}
                WHERE "Title" = '{album_title}' AND "Primary_Artist_Username" = '{username}' AND "Release_Date" = '{release_date}';
                """
                cur.execute(update_query)

def create_regular_users(num_users=50):
    """Create regular users for the database"""
    first_names = ["Alex", "Jamie", "Jordan", "Taylor", "Casey", "Riley", "Sam", "Avery", 
                  "Morgan", "Quinn", "Blake", "Cameron", "Reagan", "Emerson", "Hayden"]
    last_names = ["Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", 
                 "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White"]
    
    subscription_types = ["Free", "Premium", "Family", "Student"]
    payment_methods = ["Credit Card", "PayPal", "Bank Transfer", "Gift Card"]
    
    for i in range(num_users):
        first_name = random.choice(first_names)
        last_name = random.choice(last_names)
        username = f"{first_name.lower()}_{last_name.lower()}_{random.randint(100, 999)}"
        email = f"{username}@example.com"
        
        # Insert user
        if insert_user(username, first_name, last_name, email):
            # Insert regular user
            subscription = random.choice(subscription_types)
            payment = random.choice(payment_methods)
            renewal_date = generate_random_date(2023, 2024)
            
            query = f"""
            INSERT INTO "Regular_User" ("Username", "Subscription_Type", "Payment_Method", "Renewal_Date")
            VALUES ('{username}', '{subscription}', '{payment}', '{renewal_date}')
            ON CONFLICT ("Username") DO NOTHING;
            """
            cur.execute(query)
            print(f"Added regular user: {username}")

def create_playlists(num_playlists=20):
    """Create playlists and add songs to them"""
    # Get all regular users
    cur.execute('SELECT "Username" FROM "Regular_User" ORDER BY RANDOM() LIMIT 15;')
    users = [row[0] for row in cur.fetchall()]
    
    # Get some songs
    cur.execute("""
    SELECT "Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date" 
    FROM "Song" 
    ORDER BY RANDOM() 
    LIMIT 100;
    """)
    songs = cur.fetchall()
    
    if not users or not songs:
        print("Not enough users or songs to create playlists")
        return
    
    playlist_adjectives = ["Awesome", "Chill", "Happy", "Sad", "Energetic", "Relaxing", 
                         "Best", "Top", "Favorite", "Ultimate"]
    playlist_nouns = ["Hits", "Songs", "Tunes", "Vibes", "Beats", "Tracks", "Mix", "Collection"]
    
    for i in range(num_playlists):
        creator = random.choice(users)
        title = f"{random.choice(playlist_adjectives)} {random.choice(playlist_nouns)} {random.randint(1, 100)}"
        title = sanitize_string(title)
        creation_date = generate_random_date(2020, 2023)
        is_public = random.choice([True, False])
        description = f"A playlist of {random.randint(5, 20)} songs created by {creator}"
        
        # Insert playlist
        query = f"""
        INSERT INTO "Playlist" ("Title", "Creator_Username", "Creation_Date", "Is_Public", "Description")
        VALUES ('{title}', '{creator}', '{creation_date}', {is_public}, '{description}')
        ON CONFLICT ("Title", "Creator_Username") DO NOTHING;
        """
        try:
            cur.execute(query)
            print(f"Created playlist: {title} by {creator}")
            
            # Add songs to playlist
            playlist_songs = random.sample(songs, min(len(songs), random.randint(5, 15)))
            for song in playlist_songs:
                song_title, album_title, artist_username, album_release_date = song
                
                # Properly sanitize all fields
                song_title_safe = sanitize_string(song_title)
                album_title_safe = sanitize_string(album_title)
                artist_username_safe = sanitize_string(artist_username)
                
                query = f"""
                INSERT INTO "Includes" ("Playlist_Title", "Creator_Username", "Song_Title", 
                                      "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date")
                VALUES ('{title}', '{creator}', '{song_title_safe}', 
                       '{album_title_safe}', '{artist_username_safe}', '{album_release_date}')
                ON CONFLICT ("Playlist_Title", "Creator_Username", "Song_Title", 
                            "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") DO NOTHING;
                """
                try:
                    cur.execute(query)
                    print(f"  Added song: {song_title} to playlist {title}")
                except Exception as e:
                    print(f"  Error adding song {song_title} to playlist: {e}")
                    conn.rollback()  # Rollback this single insertion, not the whole transaction
            
            conn.commit()  # Commit after each playlist and its songs
        except Exception as e:
            print(f"Error creating playlist {title}: {e}")
            conn.rollback()
def create_follows_relationships(max_follows=100):
    """Create follows relationships between users"""
    # Get all users
    cur.execute('SELECT "Username" FROM "User";')
    users = [row[0] for row in cur.fetchall()]
    
    if len(users) < 2:
        print("Not enough users to create follows relationships")
        return
    
    follows_count = 0
    for _ in range(max_follows):
        follower = random.choice(users)
        following = random.choice(users)
        
        # Don't follow yourself
        if follower == following:
            continue
        
        query = f"""
        INSERT INTO "Follows" ("Follower", "Following")
        VALUES ('{follower}', '{following}')
        ON CONFLICT ("Follower", "Following") DO NOTHING;
        """
        try:
            cur.execute(query)
            follows_count += 1
            if follows_count % 10 == 0:
                print(f"Created {follows_count} follows relationships")
        except Exception as e:
            print(f"Error creating follows relationship: {e}")

def create_saves_relationships(max_saves=200):
    """Create saves relationships for albums and playlists"""
    # Get regular users
    cur.execute('SELECT "Username" FROM "Regular_User";')
    users = [row[0] for row in cur.fetchall()]
    
    # Get albums
    cur.execute('SELECT "Title", "Primary_Artist_Username", "Release_Date" FROM "Album";')
    albums = cur.fetchall()
    
    # Get playlists
    cur.execute('SELECT "Title", "Creator_Username" FROM "Playlist";')
    playlists = cur.fetchall()
    
    if not users or not albums:
        print("Not enough users or albums to create saves relationships")
        return
    
    # Create album saves
    saves_count = 0
    for _ in range(max_saves // 2):
        user = random.choice(users)
        if albums:
            album = random.choice(albums)
            album_title, artist_username, release_date = album
            
            try:
                # Use parameterized query to avoid issues with special characters
                query = """
                INSERT INTO "Saves_Album" ("Username", "Album_Title", "Primary_Artist_Username", "Release_Date")
                VALUES (%s, %s, %s, %s)
                ON CONFLICT ("Username", "Album_Title", "Primary_Artist_Username", "Release_Date") DO NOTHING;
                """
                cur.execute(query, (user, album_title, artist_username, release_date))
                conn.commit()  # Commit each insertion
                
                saves_count += 1
                if saves_count % 10 == 0:
                    print(f"Created {saves_count} saves relationships")
            except Exception as e:
                print(f"Error creating album save relationship: {e}")
                conn.rollback()
    
    # Create playlist saves
    for _ in range(max_saves // 2):
        user = random.choice(users)
        if playlists:
            playlist = random.choice(playlists)
            playlist_title, creator_username = playlist
            
            # Don't save your own playlist
            if user == creator_username:
                continue
            
            try:
                # Use parameterized query
                query = """
                INSERT INTO "Saves_Playlist" ("Username", "Playlist_Title", "Creator_Username")
                VALUES (%s, %s, %s)
                ON CONFLICT ("Username", "Playlist_Title", "Creator_Username") DO NOTHING;
                """
                cur.execute(query, (user, playlist_title, creator_username))
                conn.commit()  # Commit each insertion
                
                saves_count += 1
                if saves_count % 10 == 0:
                    print(f"Created {saves_count} saves relationships")
            except Exception as e:
                print(f"Error creating playlist save relationship: {e}")
                conn.rollback()
def generate_massive_includes_data(min_entries=32000, max_entries=40000):
    """Generate a large amount of playlist-song relationships to meet the requirement
    of having one table with ~32k records"""
    print(f"Generating between {min_entries} and {max_entries} playlist-song relationships...")
    
    # First verify what songs actually exist in the database
    print("Fetching existing songs from database...")
    cur.execute("""
    SELECT "Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date" 
    FROM "Song";
    """)
    available_songs = cur.fetchall()
    
    if not available_songs:
        print("No songs found in the database. Cannot create includes relationships.")
        return
    
    print(f"Found {len(available_songs)} existing songs in the database")
    
    # Get all regular users to create playlists
    cur.execute('SELECT "Username" FROM "Regular_User";')
    users = [row[0] for row in cur.fetchall()]
    
    if not users:
        print("No regular users found in the database. Cannot create playlists.")
        return
    
    # Create many playlists first
    playlist_adjectives = ["Awesome", "Chill", "Happy", "Sad", "Energetic", "Relaxing", 
                         "Best", "Top", "Favorite", "Ultimate", "Epic", "Perfect", "Amazing",
                         "Wonderful", "Brilliant", "Fantastic", "Superb", "Excellent", "Great",
                         "Supreme", "Prime", "Select", "Choice", "Quality", "Premium", "Stellar"]
    playlist_nouns = ["Hits", "Songs", "Tunes", "Vibes", "Beats", "Tracks", "Mix", "Collection",
                     "Playlist", "Compilation", "Selection", "Favorites", "Classics", "Jams",
                     "Anthems", "Bangers", "Set", "List", "Chart", "Rotation", "Picks", "Choices"]
    
    # Create a lot of playlists (500-1000)
    num_playlists = min(1000, len(users) * 20)  # Up to 20 playlists per user
    print(f"Creating {num_playlists} playlists...")
    
    playlists = []
    for i in range(num_playlists):
        creator = random.choice(users)
        title = f"{random.choice(playlist_adjectives)} {random.choice(playlist_nouns)} {random.randint(1, 9999)}"
        creation_date = generate_random_date(2020, 2023)
        is_public = random.choice([True, False])
        description = f"A playlist created by {creator}"
        
        # Insert playlist using parameterized query
        query = """
        INSERT INTO "Playlist" ("Title", "Creator_Username", "Creation_Date", "Is_Public", "Description")
        VALUES (%s, %s, %s, %s, %s)
        ON CONFLICT ("Title", "Creator_Username") DO NOTHING;
        """
        try:
            cur.execute(query, (title, creator, creation_date, is_public, description))
            playlists.append((title, creator))
            if i % 100 == 0:
                print(f"Created {i} playlists so far...")
                conn.commit()
        except Exception as e:
            print(f"Error creating playlist {title}: {e}")
            conn.rollback()
    
    conn.commit()
    print(f"Successfully created {len(playlists)} playlists")
    
    # Verify the playlists were actually created
    cur.execute('SELECT COUNT(*) FROM "Playlist";')
    playlist_count = cur.fetchone()[0]
    print(f"Total playlists in database: {playlist_count}")
    
    # Get the current count of includes relationships
    cur.execute('SELECT COUNT(*) FROM "Includes";')
    starting_count = cur.fetchone()[0]
    print(f"Starting with {starting_count} existing entries in Includes table")
    
    # Calculate how many to insert
    target_count = min_entries - starting_count
    if target_count <= 0:
        print(f"Already have {starting_count} entries, which meets the target of {min_entries}")
        return
    
    print(f"Need to insert {target_count} more entries to reach the target")
    
    # Generate entries in smaller batches to avoid memory issues
    batch_size = 100
    successful_inserts = 0
    attempts = 0
    max_attempts = target_count * 2  # Allow twice as many attempts as needed
    
    while successful_inserts < target_count and attempts < max_attempts:
        batch_entries = min(batch_size, target_count - successful_inserts)
        print(f"Attempting to insert batch of {batch_entries} entries. Progress: {successful_inserts}/{target_count}")
        
        # Create batch of entries
        insert_data = []
        for _ in range(batch_entries):
            # Select a random playlist and song
            playlist_title, creator_username = random.choice(playlists)
            song = random.choice(available_songs)
            song_title, album_title, artist_username, album_release_date = song
            
            # Add to our data list
            insert_data.append((playlist_title, creator_username, song_title, 
                              album_title, artist_username, album_release_date))
        
        # Insert batch
        if insert_data:
            try:
                # Using executemany with a parameterized query
                query = """
                INSERT INTO "Includes" ("Playlist_Title", "Creator_Username", "Song_Title", 
                                      "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date")
                VALUES (%s, %s, %s, %s, %s, %s)
                ON CONFLICT ("Playlist_Title", "Creator_Username", "Song_Title", 
                            "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") DO NOTHING;
                """
                cur.executemany(query, insert_data)
                conn.commit()
                
                # Check how many were actually inserted
                cur.execute('SELECT COUNT(*) FROM "Includes";')
                new_total = cur.fetchone()[0]
                new_inserts = new_total - (starting_count + successful_inserts)
                successful_inserts += new_inserts
                
                print(f"Inserted {new_inserts} new entries. Total progress: {successful_inserts}/{target_count}")
                
            except Exception as e:
                print(f"Error inserting includes batch: {e}")
                conn.rollback()
        
        attempts += batch_entries
    
    # Final count
    cur.execute('SELECT COUNT(*) FROM "Includes";')
    final_count = cur.fetchone()[0]
    print(f"Final count of includes entries: {final_count}")
    
    if final_count >= min_entries:
        print(f"Successfully reached the target of {min_entries} entries!")
    else:
        print(f"Could not reach the target. Only have {final_count} entries.")


def main():
    """Main function to orchestrate the data generation"""
    try:
        # Clean the database first to avoid duplicates
        clean_database()
        
        # List of artists to search for - expanded to 100+
        artists_to_search = [
            # Pop
            "Taylor Swift", "Ed Sheeran", "The Weeknd", "Ariana Grande", "Drake",
            "BTS", "Billie Eilish", "Justin Bieber", "Post Malone", "Bad Bunny",
            "Dua Lipa", "Beyoncé", "Adele", "Harry Styles", "Coldplay",
            "Imagine Dragons", "Rihanna", "Travis Scott", "Lady Gaga", "Eminem",
            "Bruno Mars", "Katy Perry", "Selena Gomez", "Shawn Mendes", "Maroon 5",
            "Doja Cat", "Olivia Rodrigo", "The Kid LAROI", "SZA", "Lizzo",
            
            # Hip Hop/Rap
            "Kendrick Lamar", "J. Cole", "Cardi B", "Megan Thee Stallion", "Lil Nas X", 
            "Jack Harlow", "Nicki Minaj", "Future", "Lil Baby", "Tyler, The Creator",
            "A$AP Rocky", "Kanye West", "21 Savage", "Snoop Dogg", "Jay-Z",
            
            # Rock/Alternative
            "Twenty One Pilots", "Arctic Monkeys", "The Killers", "Tame Impala", "Foo Fighters",
            "Green Day", "Linkin Park", "Red Hot Chili Peppers", "Queens of the Stone Age", "Muse",
            "Radiohead", "The Strokes", "The Black Keys", "Paramore", "Fall Out Boy",
            
            # Electronic/Dance
            "Calvin Harris", "Marshmello", "The Chainsmokers", "Skrillex", "David Guetta",
            "Martin Garrix", "Kygo", "Tiësto", "Zedd", "Avicii",
            
            # Country
            "Luke Combs", "Morgan Wallen", "Kacey Musgraves", "Luke Bryan", "Carrie Underwood",
            "Chris Stapleton", "Blake Shelton", "Thomas Rhett", "Keith Urban", "Maren Morris",
            
            # Latin
            "J Balvin", "Karol G", "Maluma", "Daddy Yankee", "Rosalía",
            "Anuel AA", "Ozuna", "Rauw Alejandro", "Becky G", "Nicky Jam",
            
            # Others
            "BLACKPINK", "TWICE", "Frank Ocean", "Halsey", "Lana Del Rey",
            "Charlie Puth", "H.E.R.", "Khalid", "Lewis Capaldi", "Sia"
        ]
        
        print("Starting data generation...")
        
        # 1. Fetch and insert artists
        print("\n--- Fetching and inserting artists ---")
        artist_usernames = fetch_artists_and_insert(artists_to_search)
        conn.commit()
        
        # 2. Fetch and insert albums and songs
        print("\n--- Fetching and inserting albums and songs ---")
        fetch_albums_and_songs(artist_usernames, albums_per_artist=2, tracks_per_album=10)
        conn.commit()
        
        # 3. Create regular users
        print("\n--- Creating regular users ---")
        create_regular_users(num_users=150)
        conn.commit()
        
        # 4. Create playlists
        print("\n--- Creating playlists ---")
        create_playlists(num_playlists=100)
        conn.commit()
        
        # 5. Create follows relationships
        print("\n--- Creating follows relationships ---")
        create_follows_relationships(max_follows=300)
        conn.commit()
        
        # 6. Create saves relationships
        print("\n--- Creating saves relationships ---")
        create_saves_relationships(max_saves=400)
        conn.commit()
        
        # 7. Generate massive includes data to meet the ~32k records requirement
        print("\n--- Generating massive includes data ---")
        generate_massive_includes_data(min_entries=32000, max_entries=40000)
        conn.commit()
        
        print("\nData generation complete!")
        
    except Exception as e:
        print(f"An error occurred: {e}")
        conn.rollback()
    finally:
        cur.close()
        conn.close()

if __name__ == "__main__":
    main()
