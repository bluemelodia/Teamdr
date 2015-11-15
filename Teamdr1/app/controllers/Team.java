package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.ClassRecord;
import models.TeamRecord;
import models.UserAccount;
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

/**
 * Created by anfalboussayoud on 11/11/15.
 */
public class Team extends Controller {
    public static final String currentClass = "COMS4111";

    public Result list() {
        return TODO;
    }

    public Result showTeams() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        TeamRecord td = new TeamRecord(); 
        // System.out.println(td.findAll());

        JsonNode json = toJson(td.findAll());

        return ok(team.render(json));
    }

    public Result swipeLeft() {
        return TODO;
    }

    public Result swipeRight() {
        return TODO;
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
            error = toJson("You already created a team for this class.");
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

            // Todo: we have to make sure there are acutally people in the team...

            return redirect(routes.Account.signIn());
        }
    }

    private Boolean hasTeam(String currentClass, UserAccount thisUser) {
        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (int i = 0; i < allTeams.size(); i++) {
            TeamRecord thisTeam = allTeams.get(i);
            if (!thisTeam.thisClass.equals(currentClass)) {
                continue;
            }
            String team = thisTeam.teamMembers;
            if (team == null) {
                System.out.println("Team: " + thisTeam.teamMembers + " is empty");
                continue;
            } /*if (team.contains(thisUser)) {
                return true;
            }*/
        }
        return false;
    }

    /*
     // Check if user already has a team for this class
    public static boolean hasTeam(String thisClass, UserAccount user) {
        List<TeamRecord> classTeams = TeamRecord.find.where().eq("thisClass", thisClass).findList();
        for (int i = 0; i < classTeams.size(); i++) {
            TeamRecord thisTeam = classTeams.get(i);
            ArrayList<UserAccount> teamMembers = thisTeam.teamMembers;
            if (teamMembers == null) {
                System.out.println("Team: " + thisTeam.teamMembers + " is empty");
                continue;
            }
            if (teamMembers.contains(user)) {
                return true;
            }
        }
        return false;
    }

     */
}
