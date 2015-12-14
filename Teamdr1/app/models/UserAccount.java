package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.*;
import com.avaje.ebean.Ebean;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Id;
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

    public String currentClass = "";
    public String allClasses = ""; // list of all classes user is in
    public String allRaters = ""; // list of all people who have rated this user

    // Finds all the UserAccount records on file, sorts them by usernames
    // Return as list of UserAccount records; elsewhere can iterate through the list
    // of records and process them by calling this method
    public static List<UserAccount> findAll() {
        return UserAccount.find.orderBy("username").findList();
    }

    public static String allClasses(String username) {
        UserAccount me = getUser(username);
        return me.allClasses;
    }

    // Check if this user already exists
    public static boolean exists(String username) {
        return(find.where().eq("username", username).findRowCount() == 1) ? true : false;
    }

    // Return the record with this matching username
    public static UserAccount getUser(String username) {
        return find.ref(username);
    }

	public boolean addProfile(String uname, String e) {
		this.profile.username = uname;
		this.profile.email = e;
		//ClassRecord course = new ClassRecord("4111", "DB");
		//this.profile.classes.add(course);
		return true;
	}

    public TeamRecord getTeamRecord(String classId) {
        TeamRecord teamRecord = TeamRecord.getTeamForClass(username, classId);
        return teamRecord;
    }

    public List<String> getClassList() {
        List<String> classList = new ArrayList<>();
        String[] myClasses = allClasses.split("\\|");
        for (String myClass : myClasses) {
            if (!myClass.isEmpty()) {
                classList.add(myClass);
            }
        }
        return classList;
    }

    public void addClass(String classID) {
        List<String> myClasses = getClassList();
        if (!myClasses.contains(classID)) {
            myClasses.add(classID);
        }
        this.saveClasses(myClasses);
    }

    public void removeClass(String classID) {
        List<String> myClasses = getClassList();
        myClasses.remove(classID);
        this.saveClasses(myClasses);
    }

    public void saveClasses(List<String> myClasses) {
        if (myClasses.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String myClass : myClasses) {
                sb.append("|" + myClass);
            }
            allClasses = sb.substring(1);
        } else {
            allClasses = "";
        }
        System.out.println("AllClasses: " + allClasses);
        Ebean.save(this);
    }

    public void changeCurrentClass(String newClass) {
        this.currentClass = newClass;
        Ebean.save(this);
    }

    // Pass in type of primary key, type of model; pass in class so code can figure out its fields
    private static Model.Finder<String, UserAccount> find = new Model.Finder<>(UserAccount.class);
}
