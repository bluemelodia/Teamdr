package controllers;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by anfalboussayoud on 11/11/15.
 */
public class Team extends Controller {
    public Result list() {
        return TODO;
    }

    public Result showTeams() {
        //return TODO;
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

    }

    public Result createTeam() {

    }

}
