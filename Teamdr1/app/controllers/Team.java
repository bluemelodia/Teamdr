package controllers;
import models.UserAccount;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import play.libs.Json;
import views.html.*;
import java.util.List;


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
}
