CREATE TABLE IF NOT EXISTS "User" (
	"Username" varchar(255) NOT NULL UNIQUE,
	"Password" varchar(255) NOT NULL,
	"First_Name" varchar(255) NOT NULL,
	"Last_Name" varchar(255) NOT NULL,
	"Email" varchar(255) NOT NULL,
	"Registration_Date" date NOT NULL,
	PRIMARY KEY ("Username")
);

CREATE TABLE IF NOT EXISTS "Profile_Page" (
	"URL" varchar(255) NOT NULL UNIQUE,
	"Username" varchar(255) NOT NULL,
	"Is_Public" boolean NOT NULL DEFAULT 'True',
	PRIMARY KEY ("URL")
);

CREATE TABLE IF NOT EXISTS "Regular_User" (
	"Username" varchar(255) NOT NULL UNIQUE,
	"Subscription_Type" varchar(255) NOT NULL,
	"Payment_Method" varchar(255) NOT NULL,
	"Renewal_Date" varchar(255) NOT NULL,
	PRIMARY KEY ("Username")
);

CREATE TABLE IF NOT EXISTS "Artist" (
	"Username" varchar(255) NOT NULL UNIQUE,
	"Stage_Name" varchar(255) NOT NULL,
	"Biography" varchar(255) NOT NULL,
	PRIMARY KEY ("Username")
);

CREATE TABLE IF NOT EXISTS "Album" (
	"Title" varchar(255) NOT NULL,
	"Primary_Artist_Username" varchar(255) NOT NULL,
	"Release_Date" date NOT NULL,
	"Total_Duration" bigint NOT NULL,
	PRIMARY KEY ("Title", "Primary_Artist_Username", "Release_Date")
);

CREATE TABLE IF NOT EXISTS "Album_Artist" (
	"Album_Title" varchar(255) NOT NULL,
	"Primary_Artist_Username" varchar(255) NOT NULL,
	"Release_Date" date NOT NULL,
	"Contributing_Artist_Username" varchar(255) NOT NULL,
	"Role" varchar(255) NOT NULL,
	PRIMARY KEY ("Album_Title", "Primary_Artist_Username", "Release_Date", "Contributing_Artist_Username")
);

CREATE TABLE IF NOT EXISTS "Song" (
	"Title" varchar(255) NOT NULL,
	"Album_Title" varchar(255) NOT NULL,
	"Primary_Album_Artist_Username" varchar(255) NOT NULL,
	"Album_Release_Date" date NOT NULL,
	"File_Path" varchar(255) NOT NULL UNIQUE,
	"Duration" bigint NOT NULL,
	"Lyrics" varchar(255) NOT NULL,
	"Track_Number" bigint NOT NULL,
	PRIMARY KEY ("Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date")
);

CREATE TABLE IF NOT EXISTS "Song_Artist" (
	"Song_Title" varchar(255) NOT NULL,
	"Album_Title" varchar(255) NOT NULL,
	"Album_Release_Date" date NOT NULL,
	"Primary_Album_Artist_Username" varchar(255) NOT NULL,
	"Performing_Artist_Username" varchar(255) NOT NULL,
	"Role" varchar(255) NOT NULL,
	PRIMARY KEY ("Song_Title", "Album_Title", "Album_Release_Date", "Primary_Album_Artist_Username", "Performing_Artist_Username")
);

CREATE TABLE IF NOT EXISTS "Genre" (
	"Name" varchar(255) NOT NULL UNIQUE,
	PRIMARY KEY ("Name")
);

CREATE TABLE IF NOT EXISTS "Album_Genre" (
	"Album_Title" varchar(255) NOT NULL,
	"Primary_Artist_Username" varchar(255) NOT NULL,
	"Release_Date" date NOT NULL,
	"Genre_Name" varchar(255) NOT NULL,
	PRIMARY KEY ("Album_Title", "Primary_Artist_Username", "Release_Date", "Genre_Name")
);

CREATE TABLE IF NOT EXISTS "Song_Genre" (
	"Song_Title" varchar(255) NOT NULL,
	"Album_Title" varchar(255) NOT NULL,
	"Primary_Album_Artist_Username" varchar(255) NOT NULL,
	"Album_Release_Date" date NOT NULL,
	"Genre_Name" varchar(255) NOT NULL,
	PRIMARY KEY ("Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date", "Genre_Name")
);

CREATE TABLE IF NOT EXISTS "Playlist" (
	"Title" varchar(255) NOT NULL,
	"Creator_Username" varchar(255) NOT NULL,
	"Creation_Date" date NOT NULL,
	"Is_Public" boolean NOT NULL DEFAULT 'True',
	"Description" varchar(255) NOT NULL,
	PRIMARY KEY ("Title", "Creator_Username")
);

CREATE TABLE IF NOT EXISTS "Includes" (
	"Playlist_Title" varchar(255) NOT NULL,
	"Creator_Username" varchar(255) NOT NULL,
	"Song_Title" varchar(255) NOT NULL,
	"Album_Title" varchar(255) NOT NULL,
	"Primary_Album_Artist_Username" varchar(255) NOT NULL,
	"Album_Release_Date" date NOT NULL,
	PRIMARY KEY ("Playlist_Title", "Creator_Username", "Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date")
);

CREATE TABLE IF NOT EXISTS "Follows" (
	"Follower" varchar(255) NOT NULL,
	"Following" varchar(255) NOT NULL,
	PRIMARY KEY ("Follower", "Following")
);

CREATE TABLE IF NOT EXISTS "Saves_Playlist" (
	"Username" varchar(255) NOT NULL,
	"Playlist_Title" varchar(255) NOT NULL,
	"Creator_Username" varchar(255) NOT NULL,
	PRIMARY KEY ("Username", "Playlist_Title", "Creator_Username")
);

CREATE TABLE IF NOT EXISTS "Saves_Album" (
	"Username" varchar(255) NOT NULL,
	"Album_Title" varchar(255) NOT NULL,
	"Primary_Artist_Username" varchar(255) NOT NULL,
	"Release_Date" date NOT NULL,
	PRIMARY KEY ("Username", "Album_Title", "Primary_Artist_Username", "Release_Date")
);


ALTER TABLE "Profile_Page" ADD CONSTRAINT "Profile_Page_fk1" 
FOREIGN KEY ("Username") REFERENCES "User"("Username");

ALTER TABLE "Regular_User" ADD CONSTRAINT "Regular_User_fk0" 
FOREIGN KEY ("Username") REFERENCES "User"("Username");

ALTER TABLE "Artist" ADD CONSTRAINT "Artist_fk0" 
FOREIGN KEY ("Username") REFERENCES "User"("Username");

ALTER TABLE "Album" ADD CONSTRAINT "Album_fk1" 
FOREIGN KEY ("Primary_Artist_Username") REFERENCES "Artist"("Username");

ALTER TABLE "Album_Artist" ADD CONSTRAINT "Album_Artist_fk0" 
FOREIGN KEY ("Album_Title", "Primary_Artist_Username", "Release_Date") 
REFERENCES "Album"("Title", "Primary_Artist_Username", "Release_Date");

ALTER TABLE "Album_Artist" ADD CONSTRAINT "Album_Artist_fk3" 
FOREIGN KEY ("Contributing_Artist_Username") REFERENCES "Artist"("Username");

ALTER TABLE "Song" ADD CONSTRAINT "Song_fk0" 
FOREIGN KEY ("Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") 
REFERENCES "Album"("Title", "Primary_Artist_Username", "Release_Date");

ALTER TABLE "Song_Artist" ADD CONSTRAINT "Song_Artist_fk0" 
FOREIGN KEY ("Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") 
REFERENCES "Song"("Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date");

ALTER TABLE "Song_Artist" ADD CONSTRAINT "Song_Artist_fk4" 
FOREIGN KEY ("Performing_Artist_Username") REFERENCES "Artist"("Username");

ALTER TABLE "Album_Genre" ADD CONSTRAINT "Album_Genre_fk0" 
FOREIGN KEY ("Album_Title", "Primary_Artist_Username", "Release_Date") 
REFERENCES "Album"("Title", "Primary_Artist_Username", "Release_Date");

ALTER TABLE "Album_Genre" ADD CONSTRAINT "Album_Genre_fk3" 
FOREIGN KEY ("Genre_Name") REFERENCES "Genre"("Name");

ALTER TABLE "Song_Genre" ADD CONSTRAINT "Song_Genre_fk0" 
FOREIGN KEY ("Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") 
REFERENCES "Song"("Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date");

ALTER TABLE "Song_Genre" ADD CONSTRAINT "Song_Genre_fk4" 
FOREIGN KEY ("Genre_Name") REFERENCES "Genre"("Name");

ALTER TABLE "Playlist" ADD CONSTRAINT "Playlist_fk1" 
FOREIGN KEY ("Creator_Username") REFERENCES "User"("Username");

ALTER TABLE "Includes" ADD CONSTRAINT "Includes_fk0" 
FOREIGN KEY ("Playlist_Title", "Creator_Username") 
REFERENCES "Playlist"("Title", "Creator_Username");

ALTER TABLE "Includes" ADD CONSTRAINT "Includes_fk2" 
FOREIGN KEY ("Song_Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date") 
REFERENCES "Song"("Title", "Album_Title", "Primary_Album_Artist_Username", "Album_Release_Date");

ALTER TABLE "Follows" ADD CONSTRAINT "Follows_fk0" 
FOREIGN KEY ("Follower") REFERENCES "User"("Username");

ALTER TABLE "Follows" ADD CONSTRAINT "Follows_fk1" 
FOREIGN KEY ("Following") REFERENCES "User"("Username");

ALTER TABLE "Saves_Playlist" ADD CONSTRAINT "Saves_Playlist_fk0" 
FOREIGN KEY ("Username") REFERENCES "User"("Username");

ALTER TABLE "Saves_Playlist" ADD CONSTRAINT "Saves_Playlist_fk1" 
FOREIGN KEY ("Playlist_Title", "Creator_Username") 
REFERENCES "Playlist"("Title", "Creator_Username");

ALTER TABLE "Saves_Album" ADD CONSTRAINT "Saves_Album_fk0" 
FOREIGN KEY ("Username") REFERENCES "User"("Username");

ALTER TABLE "Saves_Album" ADD CONSTRAINT "Saves_Album_fk1" 
FOREIGN KEY ("Album_Title", "Primary_Artist_Username", "Release_Date") 
REFERENCES "Album"("Title", "Primary_Artist_Username", "Release_Date");
