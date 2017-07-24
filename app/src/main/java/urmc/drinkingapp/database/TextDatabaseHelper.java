//package urmc.drinkingapp.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
///**
// * Created by litchiyang on 6/25/17.
// */
//
//public class TextDatabaseHelper extends SQLiteOpenHelper {
//    public TextDatabaseHelper(Context context) {
//        super(context, TextSchema.DATABASE_NAME, null, TextSchema.VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table " + DatabaseSchema.UsersTable.TABLE_NAME +"("
//                + DatabaseSchema.UsersTable.Cols.EMAIL + " text primary key, "
//                + DatabaseSchema.UsersTable.Cols.PASSWORD + ", "
//                + DatabaseSchema.UsersTable.Cols.FIRST_NAME + ", "
//                + DatabaseSchema.UsersTable.Cols.LAST_NAME + ", "
//                + DatabaseSchema.UsersTable.Cols.BIRTHDAY + ", "
//                + DatabaseSchema.UsersTable.Cols.PHOTO + ", "
//                + DatabaseSchema.UsersTable.Cols.HOME_TOWN + ", "
//                + DatabaseSchema.UsersTable.Cols.BIO + ")");
//
//        db.execSQL("create table " + DatabaseSchema.NetworksTable.TABLE_NAME
//                + "(_id integer primary key autoincrement, "
//                + DatabaseSchema.NetworksTable.Cols.NETWORK_ID + ", "
//                + DatabaseSchema.NetworksTable.Cols.EMAIL + ", "
//                + DatabaseSchema.NetworksTable.Cols.NETWORK_EMAIL + ")");
//
//        db.execSQL("create table " + DatabaseSchema.PostsTable.TABLE_NAME
//                + "(_id integer primary key autoincrement, "
//                + DatabaseSchema.PostsTable.Cols.POST_ID + ", "
//                + DatabaseSchema.PostsTable.Cols.EMAIL + ", "
//                + DatabaseSchema.PostsTable.Cols.TEXT + ", "
//                + DatabaseSchema.PostsTable.Cols.TIME + ", "
//                + DatabaseSchema.PostsTable.Cols.PHOTO + ")");
//    }
//
//
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//}
