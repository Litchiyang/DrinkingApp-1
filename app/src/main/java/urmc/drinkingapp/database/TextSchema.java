package urmc.drinkingapp.database;

/**
 * Created by litchiyang on 6/25/17.
 */

public class TextSchema {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "database.db";

    public static final class UsersTable {
        public static final String TABLE_NAME = "usersTable";

        public static final class Cols {
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
            public static final String FIRST_NAME = "firstname";
            public static final String LAST_NAME = "lastname";
            public static final String BIRTHDAY = "birthday";
            public static final String PHOTO = "photo";
            public static final String HOME_TOWN = "hometown";
            public static final String BIO = "BIO";
        }
    }

    public static final class NetworksTable {
        public static final String TABLE_NAME = "networksTable";

        public static final class Cols {
            public static final String NETWORK_ID = "networkid";
            public static final String EMAIL = "email";
            public static final String NETWORK_EMAIL = "networkemail";
        }
    }


    public static final class PostsTable {
        public static final String TABLE_NAME = "postsTable";

        public static final class Cols {
            public static final String POST_ID = "postid";
            public static final String EMAIL = "email";
            public static final String TEXT = "text";
            public static final String PHOTO = "photo";
            public static final String TIME = "time";
        }
    }

}

