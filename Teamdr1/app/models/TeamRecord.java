package models;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import controllers.Classes;
import models.UserAccount;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by bluemelodia on 11/11/15.
 */
@Entity
public class TeamRecord extends Model {
    @Id
    public String tid;
    @Constraints.Required
    public ArrayList<UserAccount> teamMembers;
    @Constraints.Required
    public String teamName;
    @Constraints.Required
    public String thisClass; // This is the class ID

    // Create a new team with one person
    public TeamRecord(String tid, UserAccount user, String teamName, String thisClass) {
        this.tid = tid;
        teamMembers = new ArrayList<UserAccount>();
        teamMembers.add(user);
        this.teamName = teamName;
        this.thisClass = thisClass;
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

    // Check if user already has a team for this class
    public static boolean hasTeam(String thisClass, UserAccount user) {
        List<TeamRecord> classTeams = TeamRecord.find.where().eq("thisClass", thisClass).findList();
        for (int i = 0; i < classTeams.size(); i++) {
            TeamRecord thisTeam = classTeams.get(i);
            ArrayList<UserAccount> teamMembers = thisTeam.teamMembers;
            if (teamMembers == null) continue;
            if (teamMembers.contains(user)) {
                return true;
            }
        }
        return false;
    }
}