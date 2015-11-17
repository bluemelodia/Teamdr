package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
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
    //public static String currentClass = "";

    public Result list() {
        return TODO;
    }

    //public static ArrayList<String> seenTeams = new ArrayList<String>();
    //public static String currentTeam = null;

    // Retrieve a team that you have not yet seen
    public TeamRecord showCurrentTeam(String username) {
        UserAccount thisUser = UserAccount.getUser(username);
        System.out.println("me: " + thisUser.username);

        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (TeamRecord team: allTeams) {
            //System.out.println("Team: " + team.teamName + " id: " + team.tid + " members: " + team.teamMembers);
            if (!team.thisClass.equals(thisUser.currentClass)) {
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
            if (UserAccount.haveSeenTeam(thisUser.username, team.tid)) {
                continue;
            } else {
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

    // removes teammate (whose name is entered through a form) from the current team of the user
    public Result removeTeammate() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String remUser = values.get("remove")[0];

        UserAccount thisUser = UserAccount.getUser(user);
        TeamRecord currentTeam = showCurrentTeam(user);

        TeamRecord removed = TeamRecord.removeUser(currentTeam.tid, remUser.username;

        return redirect(routes.Team.showTeams());

    }

    // removes current user from their current team
    public Result removeMe() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        UserAccount thisUser = UserAccount.getUser(user);
        TeamRecord currentTeam = showCurrentTeam(user);

        TeamRecord removed = TeamRecord.removeUser(currentTeam.tid, user.username;

        return redirect(routes.Team.showTeams());


    }

    
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

        UserAccount thisUser = UserAccount.getUser(session("connected")); // get this user
        UserAccount.changeCurrentClass(user, newClass); // set the current class
        String currentClass = thisUser.currentClass;

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
        UserAccount thisUser = UserAccount.getUser(user);
        TeamRecord currentTeam = showCurrentTeam(user);
        JsonNode currentTeamJSON;
        if (currentTeam == null) {
            System.out.println("There are no teams...leave!");
            String noTeams = "There are no other teams in this class, or you have swiped on all of them";
            JsonNode errorJson = toJson(noTeams);
            return ok(errorPage.render(errorJson)); // There are no other teams available
        } else {
            String teamDetails = "";

            // Get the team info
            TeamRecord teamToDisplay = TeamRecord.getTeam(currentTeam.tid);
            // First, eliminate all double spaces
            String[] members = (teamToDisplay.teamMembers).split(" ");
            for (int i = 0; i < members.length; i++) { // Get all member descriptions
                System.out.println("CURRENT MEMBER: [" + members[i] + "]");
                UserAccount currentUser = UserAccount.getUser(members[i]);
                System.out.println(members[i] + " username: " + currentUser.username);
                if (currentUser == null || currentUser.username.length() < 1) continue;
                teamDetails += "    " + currentUser.username.toString() + " (";
                UserProfile currentProfile = UserProfile.getUser(currentUser.username);
                teamDetails += "        " + currentProfile.description + "),";
            }
            if (teamDetails.length() > 0 && teamDetails.charAt(teamDetails.length()-1)==',') {
                teamDetails = teamDetails.substring(0, teamDetails.length()-1);
            } if (teamDetails.length() >= 255) { // Varchar can only have 255 chars
                teamDetails = teamDetails.substring(0, 251);
                teamDetails += "...";
            }
            JsonNode teamMembers = toJson(teamDetails);
            currentTeamJSON = toJson(currentTeam);
            JsonNode className = toJson(thisUser.currentClass);
            return ok(team.render(currentTeamJSON, teamMembers, className));
        }
    }

    public Result swipeRight() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount thisUser = UserAccount.getUser(user);
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        System.out.println("REQUEST: " + values);
        String thisTeam = values.get("acceptedTeam")[0];
        System.out.println("RIGHT: " + thisTeam);
        UserAccount.addSeenTeam(user, thisTeam);
        // If the user already received an invite to join this team, send them to the notifs page
        List<Notifications> notifs = Notifications.getNotifs(user);
        for (int j = 0; j < notifs.size(); j++) {
            Notifications currentNotif = notifs.get(j);
            // This user was already invited to join this team
            if (currentNotif.classID.equals(thisUser.currentClass) && currentNotif.teamID.equals(thisTeam)) {
                return redirect("http://localhost:9000/assets/notifications.html");
            }
        }

        // TeamRecord currentTeam = showCurrentTeam(user);
        //TeamRecord td = TeamRecord.getTeam(thisTeam);

        // Get the user's team
        TeamRecord myTeam = TeamRecord.getTeamForClass(user, thisUser.currentClass);

        String teamDetails = "Invited to team: " + myTeam.tid + "/n Team Members:";
        String[] members = (myTeam.teamMembers).split(" ");
        for (int i = 0; i < members.length; i++) { // Get all member descriptions
            UserAccount currentUser = UserAccount.getUser(members[i]);
            teamDetails += "    " + currentUser.username.toString() + " (";
            UserProfile currentProfile = UserProfile.getUser(currentUser.username);
            teamDetails += "        " + currentProfile.description + "),";
        }
        // Remove the comma at the end
        if (teamDetails.length() > 0 && teamDetails.charAt(teamDetails.length()-1)==',') {
            teamDetails = teamDetails.substring(0, teamDetails.length()-1);
        } if (teamDetails.length() >= 255) { // Varchar can only have 255 chars
            teamDetails = teamDetails.substring(0, 251);
            teamDetails += "...";
        }

        TeamRecord td = TeamRecord.getTeam(thisTeam);
        String[] people = (td.teamMembers).split(" ");
        for (int i = 0; i < people.length; i++) {
            UserAccount currentUser = UserAccount.getUser(people[i]);
            Notifications.createNewNotification(currentUser.username, thisUser.currentClass, 1, myTeam.tid, teamDetails);
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

    // TODO: why is the team getting added to seenTeams on refresh, yet reappears on team button click
    // Swipe left: go to the next team, mark this one as seen
    public Result swipeLeft() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        UserAccount thisUser = UserAccount.getUser(user);
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String thisTeam = values.get("rejectedTeam")[0];
        System.out.println("LEFT: " + thisTeam);
        UserAccount.addSeenTeam(user, thisTeam);
        System.out.println("This team: " + thisTeam);
        //System.out.println("SEEN TEAMS: " + seenTeams);
        return redirect(routes.Team.showTeams());
    }

    public Result showCreateTeamPage() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        UserAccount thisUser = UserAccount.getUser(user);
        JsonNode className = toJson(thisUser.currentClass);
        JsonNode error = toJson("");
        return ok(createteam.render(className, error, error));
    }

    // Create a team, validate params before adding the new team to the database
    public Result createTeam() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        UserAccount thisUser = UserAccount.getUser(user);
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String teamName = values.get("teamName")[0];
        String tid = values.get("teamID")[0];
        JsonNode error;
        JsonNode error2;
        JsonNode className = toJson(thisUser.currentClass);

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
        if (hasTeam(thisUser.currentClass, thisUser)) {
            error = toJson("You are already in a team for this class.");
            error2 = toJson("");
            return badRequest(createteam.render(className, error, error2));
        } else {
            // Create a new team
            TeamRecord.createTeamRecord(tid, thisUser, teamName, thisUser.currentClass);
            JsonNode json = toJson(thisUser.currentClass);
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
            // Redirect to the show teams page
            return redirect(routes.Team.showTeams());
        }
    }

    // Each student can only be in one team for each class
    private Boolean hasTeam(String currentClass, UserAccount thisUser) {
        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (int i = 0; i < allTeams.size(); i++) {
            TeamRecord thisTeam = allTeams.get(i);
            if (!thisTeam.thisClass.equals(thisUser.currentClass)) {
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
