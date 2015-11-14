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
        JsonNode json = toJson("COMS4111");
        return ok(createteam.render(json));
    }

    public Result createTeam() {
        return TODO;
    }

}
