package models;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import controllers.Classes;
import models.UserAccount;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class TeamRecord extends Model {
    @Id
    public String tid;
    public String teamMembers = "";
    @Constraints.Required
    public String teamName;
    @Constraints.Required
    public String thisClass; // This is the class ID

    // Create a new team with one person
    public TeamRecord(String tid, UserAccount user, String teamName, String thisClass) {
        this.tid = tid;
        if (this.teamMembers == null) {
            this.teamMembers = user.username + " ";
        } else {
            this.teamMembers = user.username + " ";
        }
        System.out.println("Adding: " + user.username + " to new team");
        System.out.println(this.teamName);
        this.teamName = teamName;
        this.thisClass = thisClass;
    }

    public static void createTeamRecord(String tid, UserAccount user, String teamName, String thisClass) {
        TeamRecord newTeam = new TeamRecord(tid, user, teamName, thisClass);
        Ebean.save(newTeam); // Save this team into the database
    }

    private static Model.Finder<String, TeamRecord> find = new Model.Finder<>(TeamRecord.class);

    public static List<TeamRecord> findAll() {
        return TeamRecord.find.orderBy("tid").findList();
    }

    public static boolean exists(String tid) {
        return(find.where().eq("tid", tid).findRowCount() == 1) ? true : false;
    }

    public static TeamRecord getTeam(String tid) {
        return find.ref(tid);
    }
}