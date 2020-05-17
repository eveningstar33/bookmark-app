package com.cmd.bookmark;

import com.cmd.bookmark.constants.BookGenre;
import com.cmd.bookmark.constants.Gender;
import com.cmd.bookmark.constants.MovieGenre;
import com.cmd.bookmark.constants.UserType;
import com.cmd.bookmark.entities.User;
import com.cmd.bookmark.entities.Bookmark;
import com.cmd.bookmark.entities.UserBookmark;
import com.cmd.bookmark.manager.BookmarkManager;
import com.cmd.bookmark.manager.UserManager;
import com.cmd.bookmark.util.IOUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class DataStore {

    private static List<User> users = new ArrayList<>();
    public static List<User> getUsers() {
        return users;
    }

    private static List<List<Bookmark>> bookmarks = new ArrayList<>();
    public static List<List<Bookmark>> getBookmarks() {
        return bookmarks;
    }

    private static List<UserBookmark> userBookmarks = new ArrayList<>();

    public static void loadData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // new com.mysql.jdbc.Driver();
            //or
            // System.setProperty("jdbc.Driver", "com.mysql.jdbc.Driver");
            //or java.sql.DriverManager
            //DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookmark_app?useSSL=false", "user1", "root");
             Statement stmt = conn.createStatement();) {
            loadUsers(stmt);
            loadWebLink(stmt);
            loadMovies(stmt);
            //loadBooks(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers(Statement stmt) throws SQLException {
        /*users[0] = UserManager.getInstance().createUser(1000, "user0@cmd.com", "test", "John", "M", Gender.MALE, UserType.USER);
        users[1] = UserManager.getInstance().createUser(1001, "user1@cmd.com", "test", "Sam", "M", Gender.MALE, UserType.USER);
        users[2] = UserManager.getInstance().createUser(1002, "user2@cmd.com", "test", "Ana",	"M", Gender.FEMALE, UserType.EDITOR);
        users[3] = UserManager.getInstance().createUser(1003, "user3@cmd.com", "test", "Sara", "M", Gender.FEMALE, UserType.EDITOR);
        users[4] = UserManager.getInstance().createUser(1004, "user4@cmd.com", "test", "Dan", "M", Gender.MALE, UserType.CHIEF_EDITOR);
         */
        // String[] data = new String[TOTAL_USER_COUNT];

        String query = "Select * from User";
        ResultSet rs = stmt.executeQuery(query);

        List<Bookmark> bookmarkList = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            int gender_id = rs.getInt("gender_id");
            Gender gender = Gender.values()[gender_id];
            int user_type_id = rs.getInt("user_type_id");
            UserType userType = UserType.values()[user_type_id];

            User user =  UserManager.getInstance().createUser(id, email,password, firstName, lastName, gender, userType);
            users.add(user);
        }
    }

    private static void loadWebLink(Statement stmt) throws SQLException {
        /*bookmarks[0][0] = BookmarkManager.getInstance().createWebLink(2000, "Taming Tiger, Part 2", "http://www.javaworld.com/article/2072759/core-java/taming-tiger--part-2.html", "http://www.javaworld.com");
        bookmarks[0][1] = BookmarkManager.getInstance().createWebLink(2001, "How do I import a pre-existing Java project into Eclipse and get up and running?", "http://stackoverflow.com/questions/142863/how-do-i-import-a-pre-existing-java-project-into-eclipse-and-get-up-and-running", "http://www.stackoverflow.com");
        bookmarks[0][2] = BookmarkManager.getInstance().createWebLink(2002, "Interface vs Abstract Class", "http://mindprod.com/jgloss/interfacevsabstract.html","http://mindprod.com");
        bookmarks[0][3] = BookmarkManager.getInstance().createWebLink(2003, "NIO tutorial by Greg Travis", "http://cs.brown.edu/courses/cs161/papers/j-nio-ltr.pdf", "http://cs.brown.edu");
        bookmarks[0][4] = BookmarkManager.getInstance().createWebLink(2004, "Virtual Hosting and Tomcat", "http://tomcat.apache.org/tomcat-6.0-doc/virtual-hosting-howto.html", "http://tomcat.apache.org");
         */

        String query = "Select * from WebLink";
        ResultSet rs = stmt.executeQuery(query);

        List<Bookmark> bookmarkList = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            String url = rs.getString("url");
            String host = rs.getString("host");
            Bookmark bookmark = BookmarkManager.getInstance().createWebLink(id, title, url, host);
            bookmarkList.add(bookmark);
        }
        bookmarks.add(bookmarkList);
    }

    private static void loadMovies(Statement stmt) throws SQLException {
        /* bookmarks[1][0] = BookmarkManager.getInstance().createMovie(3000, "Citizen Kane", "", 1941, new String[] {"Orson Welles", "Joseph Cotten"}, new String[] {"Orson Welles"}, MovieGenre.CLASSICS, 8.5);
         bookmarks[1][1] = BookmarkManager.getInstance().createMovie(3001, "The Grapes of Wrath", "", 1940, new String[] {"Henry Fonda", "Jane Darwell"}, new String[] {"John Ford"}, MovieGenre.CLASSICS, 8.2);
         bookmarks[1][2] = BookmarkManager.getInstance().createMovie(3002, "A Touch of Greatness", "", 2004, new String[] {"Albert Cullum"}, new String[] {"Leslie Sullivan"}, MovieGenre.DOCUMENTARIES, 7.3);
         bookmarks[1][3] = BookmarkManager.getInstance().createMovie(3003, "The Big Bang Theory", "", 2007, new String[] {"Kaley Cuoco", "Jim Parsons"}, new String[] {"Chuck Lorre", "Bill Prady"}, 	MovieGenre.TV_SHOWS, 8.7);
         bookmarks[1][4] = BookmarkManager.getInstance().createMovie(3004, "Ikiru", "", 1952, new String[] {"Takashi Shimura", "Minoru Chiaki"}, new String [] {"Akira Kurosawa"}, MovieGenre.FOREIGN_MOVIES,	8.4);
         */

        String query = "Select m.id, title, release_year, GROUP_CONCAT(DISTINCT a.name SEPARATOR ',') AS cast, GROUP_CONCAT(DISTINCT d.name SEPARATOR ',') AS directors, movie_genre_id, imdb_rating"
                + " from Movie m, Actor a, Movie_Actor ma, Director d, Movie_Director md "
                + "where m.id = ma.movie_id and ma.actor_id = a.id and "
                + "m.id = md.movie_id and md.director_id = d.id group by m.id";
        ResultSet rs = stmt.executeQuery(query);

        List<Bookmark> bookmarkList = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            int releaseYear = rs.getInt("release_year");
            String[] cast = rs.getString("cast").split(",");
            String[] directors = rs.getString("directors").split(",");
            int genre_id = rs.getInt("movie_genre_id");
            MovieGenre genre = MovieGenre.values()[genre_id];
            double imdbRating = rs.getDouble("imdb_rating");

            Bookmark bookmark = BookmarkManager.getInstance().createMovie(id, title, "", releaseYear, cast, directors, genre, imdbRating/*, values[7]*/);
            bookmarkList.add(bookmark);
        }
        bookmarks.add(bookmarkList);
    }

    private static void loadBooks(Statement stmt) throws SQLException {

        String query = "Select b.id, title, publication_year, p.name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, amazon_rating, created_date"
                + " from Book b, Publisher p, Author a, Book_Author ba "
                + "where b.publisher_id = p.id and b.id = ba.book_id and ba.author_id = a.id group by b.id";

        ResultSet rs = stmt.executeQuery(query);

        List<Bookmark> bookmarkList = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            String imageUrl = rs.getString("imageUrl");
            int publicationYear = rs.getInt("publication_year");
            String publisher = rs.getString("name");
            String[] authors = rs.getString("authors").split(",");
            int genre_id = rs.getInt("book_genre_id");
            BookGenre genre = BookGenre.values()[genre_id];
            double amazonRating = rs.getDouble("amazon_rating");

            Date createdDate = rs.getDate("created_date");
            System.out.println("createdDate: " + createdDate);
            Timestamp timeStamp = rs.getTimestamp(8);
            System.out.println("timeStamp: " + timeStamp);
            System.out.println("localDateTime: " + timeStamp.toLocalDateTime());

            System.out.println("id: " + id + ", title: " + title + ", imageUrl: " + imageUrl + ", publication year: " + publicationYear + ", publisher: " + publisher + ", authors: " + String.join(", ", authors) + ", genre: " + genre + ", amazonRating: " + amazonRating);

            Bookmark bookmark = BookmarkManager.getInstance().createBook(id, title, imageUrl, publicationYear, publisher, authors, genre, amazonRating);
            bookmarkList.add(bookmark);
        }
        bookmarks.add(bookmarkList);
    }


    public static void add(UserBookmark userBookmark) {
        userBookmarks.add(userBookmark);
    }
}
