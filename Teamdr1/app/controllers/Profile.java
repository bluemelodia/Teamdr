package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.UserAccount;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.profile;

import static play.libs.Json.*;

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
        UserAccount getUser = UserAccount.getUser(user);

        JsonNode json = toJson(getUser);
        return ok(profile.render(json));
    }
}
