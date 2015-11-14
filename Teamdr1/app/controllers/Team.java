package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.ClassRecord;
import models.TeamRecord;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.update_profile;
import play.data.Form;
import java.util.*;
import static play.libs.Json.*;
/**
 * Created by anfalboussayoud on 11/11/15.
 */
public class Team extends Controller {
    public Result list() {
        return TODO;
    }

    public Result showTeams() {
        return TODO;
        /*String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        return ok(team.render());*/
    }

    public Result swipeLeft() {
        return TODO;
    }

    public Result swipeRight() {
        return TODO;
    }

    public Result showCreateTeamPage() {
        JsonNode className = toJson("COMS4111");
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
        JsonNode className = toJson("COMS4111");

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

        // Create a new team and add to the database
        // Fill in user, class, etc.
        // User cannot have more than one team for this class

        JsonNode json = toJson("COMS4111");
        error = toJson("");
        error2 = toJson("");
        return ok(createteam.render(json, error, error2));
    }

}
