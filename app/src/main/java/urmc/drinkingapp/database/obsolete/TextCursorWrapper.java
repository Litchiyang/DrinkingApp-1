//package urmc.drinkingapp.database;
//
//
//import android.database.Cursor;
//import android.database.CursorWrapper;
//
//import java.util.UUID;
//
//import urmc.drinkingapp.model.User;
///**
// * Created by litchiyang on 6/25/17.
// */
//
//public class TextCursorWrapper extends CursorWrapper {
//
//
//    //Constructor for cursor
//    public UsersCursorWrapper(Cursor cursor) {
//        super(cursor);
//    }
//
//    //Get the users
//    public Users getUsers() {
//
//        if(getCount() == 0){
//            return null;
//        }
//
//        String email = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.EMAIL));
//        String password = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.PASSWORD));
//        String firstName = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.FIRST_NAME));
//        String lastName = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.LAST_NAME));
//        String birthday = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.BIRTHDAY));
//        String photo = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.PHOTO));
//        String hometown = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.HOME_TOWN));
//        String BIO = getString(getColumnIndex(DatabaseSchema.UsersTable.Cols.BIO));
//
//        Users user = new Users();
//        user.setEmail(email);
//        user.setPassword(password);
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setBirthday(birthday);
//        user.setPhoto(photo);
//        user.setHomeTown(hometown);
//        user.setBIO(BIO);
//
//        return user;
//    }
//
//    public Network getNetwork(){
//        UUID id = UUID.fromString(getString(getColumnIndex(DatabaseSchema.NetworksTable.Cols.NETWORK_ID)));
//        String email = getString(getColumnIndex(DatabaseSchema.NetworksTable.Cols.EMAIL));
//        String networkEmail = getString(getColumnIndex(DatabaseSchema.NetworksTable.Cols.NETWORK_EMAIL));
//
//        Network network = new Network(id);
//        network.setEmail(email);
//        network.setNetworkEmail(networkEmail);
//
//        return network;
//    }
//
//    public Posts getPost(){
//        UUID post_id = UUID.fromString(getString(getColumnIndex(DatabaseSchema.PostsTable.Cols.POST_ID)));
//        String email = getString(getColumnIndex(DatabaseSchema.PostsTable.Cols.EMAIL));
//        String photo = getString(getColumnIndex(DatabaseSchema.PostsTable.Cols.PHOTO));
//        String time = getString(getColumnIndex(DatabaseSchema.PostsTable.Cols.TIME));
//        String text = getString(getColumnIndex(DatabaseSchema.PostsTable.Cols.TEXT));
//
//        Posts post = new Posts(post_id);
//        post.setEmail(email);
//        post.setPhoto(photo);
//        post.setText(text);
//        post.setTime(time);
//
//        return post;
//    }
//
//
//
//}
