package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.ClassRecord;
import models.TeamRecord;
import models.UserAccount;
import models.UserProfile;
import models.TeamRecord;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.update_profile;
import play.data.Form;
import java.util.*;
import static play.libs.Json.*;
import play.libs.Json.*;
import javax.persistence.*;

import static play.libs.Json.toJson;
// TODO: if the user does not have a team anymore, redirect them to the create Team page
/**
 * Created by anfalboussayoud on 11/11/15.
 */
public class Team extends Controller {
    public static String currentClass = "";

    public Result list() {
        return TODO;
    }

    public static ArrayList<String> seenTeams = new ArrayList<String>();
    public static String thisTeam = null;

    // Retrieve a team that you have not yet seen
    public TeamRecord showCurrentTeam(String username) {
        UserAccount thisUser = UserAccount.getUser(username);
        System.out.println("me: " + thisUser.username);

        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (TeamRecord team: allTeams) {
            //System.out.println("Team: " + team.teamName + " id: " + team.tid + " members: " + team.teamMembers);
            if (!team.thisClass.equals(currentClass)) {
                continue;
            }
            System.out.println(team.teamMembers);
            String[] teamMembers = (team.teamMembers).split(" ");
            Boolean myTeam = false;
            for (int i = 0; i < teamMembers.length; i++) {
                if (teamMembers[i].equals(thisUser.username)) {
                    System.out.println("This is my team...");
                    myTeam = true;
                }
            }
            if (myTeam) continue; // don't return your own team!
            Boolean contains = false;
            if (seenTeams == null) continue;
            for (int i = 0; i < seenTeams.size(); i++) {
                System.out.println("Seen ids: " + seenTeams.get(i));
                if (seenTeams.get(i).equals(team.tid)) {
                    contains = true;
                }
            }
            if (contains) {
                continue;
            } else {
                // seenTeams.add(team); // add this team to the ones you have already seen
                System.out.println("Returning team: " + team.teamName);
                return team;
            }
        }
        System.out.println("There are no other teams");
        return null;
    }

    // User has already gone through all teams, reset
    /*private void resetTeams() {
        seenTeams.clear();
    }*/

    public Result showError() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        JsonNode errorJson = toJson("I think you are lost...");
        return ok(errorPage.render(errorJson));
    }

    // This method helps pick the correct class, based on form submission, for the team search
    public Result setCurrentClass() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        System.out.println("HERE");
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        System.out.println("VALUES: " + values);
        String newClass = values.get("myClass")[0];
        System.out.println("currentClass changed to: " + newClass);
        currentClass = newClass;

        UserAccount thisUser = UserAccount.getUser(session("connected")); // get this user

        // If the user does not have a team for this class, have them make a new team
        if (!hasTeam(currentClass, thisUser)) {
            JsonNode className = toJson(currentClass);
            JsonNode error = toJson("");
            return ok(createteam.render(className, error, error));
        }
        return redirect(routes.Team.showTeams());
    }

    public Result showTeams() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        TeamRecord currentTeam = showCurrentTeam(user);
        JsonNode currentTeamJSON;
        if (currentTeam == null) {
            System.out.println("There are no teams...leave!");
            String noTeams = "There are no other teams in this class, or you have swiped on all of them";
            JsonNode errorJson = toJson(noTeams);
            return ok(errorPage.render(errorJson)); // There are no other teams available
        } else {
            thisTeam = currentTeam.tid;
            System.out.println("CURRENT TEAM: " + currentTeam.tid + " THIS TEAM: " + thisTeam);
            String teamDetails = "";

            // Get the team info
            TeamRecord teamToDisplay = TeamRecord.getTeam(thisTeam);
            // First, eliminate all double spaces
            String[] members = (teamToDisplay.teamMembers).split(" ");
            for (int i = 0; i < members.length; i++) { // Get all member descriptions
                System.out.println("CURRENT MEMBER: [" + members[i] + "]");
                UserAccount currentUser = UserAccount.getUser(members[i]);
                System.out.println(members[i] + " username: " + currentUser.username);
                if (currentUser == null || currentUser.username.length() < 1) continue;
                teamDetails += "    " + currentUser.username.toString() + "/n";
                UserProfile currentProfile = UserProfile.getUser(currentUser.username);
                teamDetails += "        " + currentProfile.description + "/n/n";
            }
            JsonNode teamMembers = toJson(teamDetails);
            currentTeamJSON = toJson(currentTeam);
            JsonNode className = toJson(currentClass);
            return ok(team.render(currentTeamJSON, teamMembers, className));
        }
    }

    public Result swipeRight() {
        seenTeams.add(thisTeam);

        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        // TeamRecord currentTeam = showCurrentTeam(user);
        TeamRecord td = TeamRecord.getTeam(thisTeam);
        String teamDetails = "";
        String[] members = (td.teamMembers).split(" ");
        for (int i = 0; i < members.length; i++) { // Get all member descriptions
            UserAccount currentUser = UserAccount.getUser(members[i]);
            teamDetails += "    " + currentUser.username.toString() + "/n";
            UserProfile currentProfile = UserProfile.getUser(currentUser.username);
            teamDetails += "        " + currentProfile.description + "/n/n";
        }

        //update team with currently shown team and user
        //td = td.updateTeam(thisTeam, user);
        //System.out.println("new team " + td.teamMembers);
        //td.save();
        return redirect(routes.Team.showTeams());
    }

    public Result sendRequest() {
        return TODO;
    }

    public Result mergeTeams(String team1, String team2) {
        return TODO;
    }

    // Swipe left: go to the next team, mark this one as seen
    public Result swipeLeft() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        seenTeams.add(thisTeam);
        //System.out.println("SEEN TEAMS: " + seenTeams);
        return redirect(routes.Team.showTeams());
    }

    public Result showCreateTeamPage() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };

        JsonNode className = toJson(currentClass);
        JsonNode error = toJson("");
        return ok(createteam.render(className, error, error));
    }

    // Create a team, validate params before adding the new team to the database
    public Result createTeam() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String teamName = values.get("teamName")[0];
        String tid = values.get("teamID")[0];
        JsonNode error;
        JsonNode error2;
        JsonNode className = toJson(currentClass);

        if (teamName.length() < 1 && tid.length() < 1) {
            error = toJson("TeamName is required.");
            error2 = toJson("TeamID is required.");
            return badRequest(createteam.render(className, error, error2));
        } else if (teamName.length() < 1) {
            error = toJson("TeamName is required.");
            error2 = toJson("");
            return badRequest(createteam.render(className, error, error2));
        } else if (tid.length() < 1) {
            error = toJson("");
            error2 = toJson("TeamID is required.");
            return badRequest(createteam.render(className, error, error2));
        }

        // Check to see if the team already exists
        if (TeamRecord.exists(tid)) {
            error = toJson("Team ID already exists!");
            error2 = toJson("");
            return badRequest(createteam.render(className, error, error2));
        }

        // User cannot have more than one team for this class
        UserAccount thisUser = UserAccount.getUser(session("connected")); // get this user
        if (hasTeam(currentClass, thisUser)) {
            error = toJson("You are already in a team for this class.");
            error2 = toJson("");
            return badRequest(createteam.render(className, error, error2));
        } else {
            // Create a new team
            TeamRecord.createTeamRecord(tid, thisUser, teamName, currentClass);
            JsonNode json = toJson(currentClass);
            error = toJson("");
            error2 = toJson("");

            // Code to check the created teams
            List<TeamRecord> allTeams = TeamRecord.findAll();
            for (int i = 0; i < allTeams.size(); i++) {
                TeamRecord team = allTeams.get(i);
                System.out.println("Name: " + team.teamName);
                System.out.println("Team: " + team.teamMembers);
                System.out.println("Class: " + team.thisClass);
                System.out.println();
            }
            // Redirect to the profile page
            return redirect(routes.Profile.viewProfile());
        }
    }

    // Each student can only be in one team for each class
    private Boolean hasTeam(String currentClass, UserAccount thisUser) {
        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (int i = 0; i < allTeams.size(); i++) {
            TeamRecord thisTeam = allTeams.get(i);
            if (!thisTeam.thisClass.equals(currentClass)) {
                continue;
            }
            String team = thisTeam.teamMembers;
            if (team == null || team.length() < 1) continue;
            String[] teamMates = team.split(" ");
            for (int j = 0; j < teamMates.length; j++) {
                if (teamMates[j].equals(thisUser.username)) {
                    return true;
                }
            }
        }
        return false;
    }
}
