package models;
import com.avaje.ebean.Model;
import controllers.Classes;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by Bailey on 11/13/15.
 */
public class UserProfile {
    @Id
    @Constraints.Required
	public String username;
	public String email;
	public String pic_url;
    public ClassRecord[] classes;
    public String description;
	
	// Pass in type of primary key, type of model; pass in class so code can figure out its fields
    private static Model.Finder<String, UserProfile> find = new Model.Finder<>(UserProfile.class);

    // Finds all the UserProfile records on file, sorts them by usernames
    // Return as list of UserProfile records; elsewhere can iterate through the list
    // of records and process them by calling this method
    public static List<UserProfile> findAll() {
        return UserProfile.find.orderBy("email").findList();
    }

    // Check if this user already exists
    public static boolean exists(String email) {
        return(find.where().eq("email", email).findRowCount() == 1) ? true : false;
    }

    // Return the record with this matching username
    public static UserProfile getUser(String email) {
        return find.ref(email);
    }
	
	public UserProfile updateProfile(String username){ 
		this.username = username;
		ClassRecord course = new ClassRecord();
		course.classID = "COMS 4111";
		course.className = "Introduction to Databases";
		this.classes = new ClassRecord[5];
		this.classes[0] = course;
		System.out.println(this.classes[0].classID);
		return this;
	}
}