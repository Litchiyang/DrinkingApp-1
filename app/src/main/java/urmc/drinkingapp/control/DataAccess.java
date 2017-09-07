package urmc.drinkingapp.control;

import java.util.UUID;

/**
 * Class for communicating between data layer and bussiness layer
 * Since the current data is stored on firebase, the class extends FirebaseDAO
 */

public class DataAccess extends FirebaseDAO {
    public DataAccess(){
        super();
    }

    public DataAccess(UUID uuid){
        super(uuid.toString());
    }

    public DataAccess(String id){
        super(id);
    }

}
