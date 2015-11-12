package models;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class UserAccount extends Model {
    @Id
    public String username; // Primary key
    public String password;

    // Pass in type of primary key, type of model; pass in class so code can figure out its fields
    private static Model.Finder<String, UserAccount> find = new Model.Finder<>(UserAccount.class);

    // Finds all the UserAccount records on file, sorts them by usernames
    // Return as list of UserAccount records; elsewhere can iterate through the list
    // of records and process them by calling this method
    public static List<UserAccount> findAll() {
        return UserAccount.find.orderBy("username").findList();
    }
}