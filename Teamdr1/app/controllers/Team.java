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
        return ok(team.render());
    }

    public Result swipeLeft() {
        return TODO;
    }

    public Result swipeRight() {
        return TODO;
    }
}
