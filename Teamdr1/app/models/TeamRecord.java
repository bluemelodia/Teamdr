package models;
import controllers.Classes;
import models.UserAccount;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class TeamRecord extends Model {
    @Id
    String username;
    @Constraints.Required
    String[] rejectedTeams;
    @Constraints.Required
    String[] teammates;

    // Pass in type of primary key, type of model; pass in class so code can figure out its fields
    /*private static Model.Finder<String, UserAccount> find = new Model.Finder<>(UserAccount.class);*/

    // Finds all the UserAccount records on file, sorts them by usernames
    // Return as list of UserAccount records; elsewhere can iterate through the list
    // of records and process them by calling this method
   /* public static List<UserAccount> findAll() {
        return UserAccount.find.orderBy("username").findList();
    }*/

    // Check if this user already exists
   /* public static boolean exists(String username) {
        return(find.where().eq("username", username).findRowCount() == 1) ? true : false;
    }*/

    // Return the record with this matching username
    /*public static UserAccount getUser(String username) {
        return find.ref(username);
    }*/
}