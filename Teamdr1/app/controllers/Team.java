package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.ClassRecord;
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
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        return ok(team.render());
    }

    public Result swipeLeft() {
        return TODO;
    }

    public Result swipeRight() {
        return TODO;
    }

    public Result showCreateTeamPage() {
        JsonNode json = toJson("COMS4111");
        return ok(createteam.render(json));
    }

    public Result createTeam() {
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String teamName = values.get("teamName")[0];
        String tid = values.get("teamID")[0];
        System.out.println(teamName + " " + tid);
        JsonNode json = toJson("COMS4111");
        return ok(createteam.render(json));
    }

}
