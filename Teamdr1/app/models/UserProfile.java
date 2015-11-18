package models;
import com.avaje.ebean.Model;
import controllers.Classes;
import play.data.validation.Constraints;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.transform.Result;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Bailey on 11/13/15.
 */
@Entity
public class UserProfile extends Model{
    @Id
    @Constraints.Required
	public String username;
	public String email;
	public String pic_url;
	@OneToMany(cascade = CascadeType.REMOVE)
    public ArrayList<ClassRecord> classes = new ArrayList<ClassRecord>();
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
    public static UserProfile getUser(String username) {
        return find.ref(username);
    }

	// Return the record with this matching username
	public static int getSize(String username) {
		UserProfile myProfile = UserProfile.getUser(username);
		return myProfile.classes.size();
	}
	public static ClassRecord getClass(String username, int n) {
        UserProfile profile =find.ref(username);
		ClassRecord c = new ClassRecord("4111", "DB");
		profile.classes.add(c);
		ClassRecord course = profile.classes.get(n);
		
		return course;	
    }
	
	public boolean addClass(String cid, String cname){ 
		ClassRecord course = new ClassRecord(cid, cname);
		//course.classID = cid;
		//course.className = cname;
		course.save();
		this.classes.add(course);
		int size = classes.size();
		this.classes.get(size-1).save();
		System.out.println(this.classes.get(0).classID);
		return true;
	}
	
	public UserProfile updateProfile(String uname, String e, String d){
		this.username = uname;
		this.email = e;
		this.description = d;
		ClassRecord course = new ClassRecord("4111", "DB");
		//course.classID = "COMS 4111";
		//course.className = "Introduction to Databases";
		this.classes.add(course);
		System.out.println(this.classes.get(0).classID);
		return this;
	}
}