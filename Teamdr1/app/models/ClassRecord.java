package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by bluemelodia on 11/12/15.
 */
@Entity
public class ClassRecord extends Model {
    @Id
    @Constraints.Required
    public String classID;
    @Constraints.Required
    public String className;
	//public UserAccount[] enrolledStudents;
	public TeamRecord team;

	
    // Pass in type of primary key, type of model; pass in class so code can figure out its fields
    private static Model.Finder<String, ClassRecord> find = new Model.Finder<>(ClassRecord.class);

    // Finds all the UserAccount records on file, sorts them by usernames
    // Return as list of UserAccount records; elsewhere can iterate through the list
    // of records and process them by calling this method
    public static List<ClassRecord> findAll() {
        return ClassRecord.find.orderBy("classID").findList();
    }

    // Check if this user already exists
    public static boolean exists(String classID) {
        return(find.where().eq("classID", classID).findRowCount() == 1) ? true : false;
    }

    // Return the record with this matching username
    public static ClassRecord getClass(String classID) {
        return find.ref(classID);
    }
}