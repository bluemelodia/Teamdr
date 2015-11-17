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
    //public String seenTeams = "";
    public String currentClass = "";

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
    /*
    public static void addSeenTeam(String username, String teamID) {
        UserAccount me = getUser(username);
        String[] haveSeen = (me.seenTeams).split(" ");
        ArrayList<String> alreadySeen = new ArrayList<>();
        for (int i = 0; i < haveSeen.length; i++) {
            String currentTeam = haveSeen[i].trim();
            alreadySeen.add(currentTeam);
        }
        if (!alreadySeen.contains(teamID)) {
            me.seenTeams += teamID + " ";
        }
        Ebean.save(me);
    }

    public static boolean haveSeenTeam(String username, String teamID) {
        UserAccount me = getUser(username);
        String[] haveSeen = (me.seenTeams).split(" ");
        ArrayList<String> alreadySeen = new ArrayList<>();
        for (int i = 0; i < haveSeen.length; i++) {
            String currentTeam = haveSeen[i].trim();
            alreadySeen.add(currentTeam);
        }
        if (alreadySeen.contains(teamID)) {
            return true;
        }
        return false;
    }

    // If a team got deleted, make each user delete this team from the seen team list so the ID can be reused
    public static void removeDeletedTeam(String teamID) {
        List<UserAccount> allUsers = findAll();
        for (int i = 0; i < allUsers.size(); i++) {
            UserAccount me = allUsers.get(i);
            String[] iSaw = (me.seenTeams).split(" ");
            ArrayList<String> alreadySeen = new ArrayList<>();
            for (int j = 0; j < alreadySeen.size(); j++) {
                String currentTeam = alreadySeen.get(j).trim();
                alreadySeen.add(currentTeam);
            }
            if (!alreadySeen.contains(teamID)) {
                alreadySeen.remove(teamID);

                // Rewrite the string with the remaining seen teams
                me.seenTeams = "";
                for (int k = 0; k < alreadySeen.size(); k++) {
                    me.seenTeams += alreadySeen.get(k) + " ";
                }
                Ebean.save(me);
            }
        }
    }*/

    public static void changeCurrentClass(String username, String newClass) {
        UserAccount me = getUser(username);
        me.currentClass = newClass;
        Ebean.save(me);
    }
    /*
    public static String getSeenTeams(String username) {
        UserAccount me = getUser(username);
        return me.seenTeams;
    }*/
}
