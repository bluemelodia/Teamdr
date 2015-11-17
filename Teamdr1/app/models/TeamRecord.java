package models;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import controllers.Classes;
import models.UserAccount;
import models.Notifications;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;
import java.lang.reflect.Array;
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

    // Get the user's specific team
    public static TeamRecord getTeamForClass(String username, String thisClass) {
        List<TeamRecord> records = find.where().eq("thisClass", thisClass).findList();
        for (int i = 0; i < records.size(); i++) {
            TeamRecord currentRecord = records.get(i);
            String[] teamMembers = (currentRecord.teamMembers).split(" ");
            for (int j = 0; j < teamMembers.length; j++) {
                String currentMember = teamMembers[j].trim();
                if (currentMember.equals(username)) {
                    return currentRecord;
                }
            }
        }
        return null;
    }

        // Retrieve a team that you have not yet seen
    public static TeamRecord userTeam(String username) {
        UserAccount thisUser = UserAccount.getUser(username);
        System.out.println("me: " + thisUser.username);
        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (TeamRecord team: allTeams) {

            System.out.println(team.teamMembers);
            String[] teamMembers = (team.teamMembers).split(" ");
            
            for (int i = 0; i < teamMembers.length; i++) {
                if (teamMembers[i].equals(thisUser.username)) {
                    System.out.println("This is my team...");
                    return team;
                }
            }

        }
        return null;
    }

    // Merge two teams
    public TeamRecord updateTeam(String tid, String tid2, String classID){
        TeamRecord requesterTeam = getTeam(tid);
        TeamRecord receiverTeam = getTeam(tid2);
        String[] teamMembers = (receiverTeam.teamMembers).split(" ");
        String[] requesterTeamMembers = (requesterTeam.teamMembers).split(" ");
        ArrayList<String> originalTeam = new ArrayList<String>();
        for (int j = 0; j < requesterTeamMembers.length; j++) {
            originalTeam.add(requesterTeamMembers[j].trim());
        }

        for (int i = 0; i < teamMembers.length; i++) { // add the receiver team members to requester team
            if (!originalTeam.contains(teamMembers[i].trim())) {
                requesterTeam.teamMembers += teamMembers[i].trim() + " ";
            }
        }

        // remove all notifs for the merged team that is related to the original team
        for (int k = 0; k < teamMembers.length; k++) {
            String thisMember = teamMembers[k].trim();
            List<Notifications> listNotifs = Notifications.getNotifs(thisMember);
            if (listNotifs.size() < 1) continue;
            for (int l = 0; l < listNotifs.size(); l++) {
                Notifications thisNotif = listNotifs.get(l);
                if (thisNotif.classID.equals(classID)) {
                    Ebean.delete(thisNotif);
                }
            }
        }

        System.out.println("New team: " + requesterTeam.teamMembers);
        // remove the receiver team
        Ebean.delete(receiverTeam);

        return requesterTeam; // this team now an aggregate
        /*
        this.tid = tid;
        this.teamName = getTeam(tid).teamName;
        TeamRecord curTeam = TeamRecord.userTeam(uname);
        
        //if they already add a team add all those teammembers
        if (curTeam != null) {
            String[] teamMembers = (curTeam.teamMembers).split(" ");
            String newMembers = "";

            for (int i = 0; i < teamMembers.length; i++) {
                this.teamMembers = this.teamMembers + " " + teamMembers[i];
            }

            curTeam.delete();
        
        //if they don't already have a team just add their name
        } else {

            this.teamMembers = getTeam(tid).teamMembers + " " + uname;
        }

        return this;*/
    }


    // updates the database by removing the team member uname from the team tid
    public static TeamRecord removeUser(String tid, String uname){
        TeamRecord team = getTeam(tid);
        String[] teamMembers = (team.teamMembers).split(" ");
        String newMembers = "";
        for (int j = 0; j < teamMembers.length; j++) {
            if (!teamMembers[j].equals(uname)){
                newMembers = newMembers + " " + teamMembers[j];
            } 
        }

        team.teamMembers = newMembers; 
        team.save();

        return team; 



    }
}