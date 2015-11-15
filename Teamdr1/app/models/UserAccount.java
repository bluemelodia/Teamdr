package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.*;
import com.avaje.ebean.Ebean;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import models.TeamRecord;
import java.util.List;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class UserAccount extends Model {
    @Id
    @Constraints.Required
    public String username; // Primary key
    @Constraints.Required
    public String password; // System will not allow invalid data save
	@Constraints.Required
	@OneToOne(cascade = CascadeType.REMOVE)
	public UserProfile profile = new UserProfile();

    // Pass in type of primary key, type of model; pass in class so code can figure out its fields
    private static Model.Finder<String, UserAccount> find = new Model.Finder<>(UserAccount.class);

    // Finds all the UserAccount records on file, sorts them by usernames
    // Return as list of UserAccount records; elsewhere can iterate through the list
    // of records and process them by calling this method
    public static List<UserAccount> findAll() {
        return UserAccount.find.orderBy("username").findList();
    }

    // Check if this user already exists
    public static boolean exists(String username) {
        return(find.where().eq("username", username).findRowCount() == 1) ? true : false;
    }

    // Return the record with this matching username
    public static UserAccount getUser(String username) {
        return find.ref(username);
    }
	
	public boolean addProfile(String uname, String e){
		this.profile.username = uname;
		this.profile.email = e;
		ClassRecord course = new ClassRecord("4111", "DB");
		this.profile.classes.add(course);
		return true;
	}
	
}
