package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;

import views.html.profile;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Profile extends Controller {
    public Result viewProfile() {
        String user = session("connected");
        System.out.println("Parameter: " + user);
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        return ok(profile.render());
    }
}
