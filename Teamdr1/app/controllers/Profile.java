package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.UserAccount;
import models.UserProfile;
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
 * Created by bluemelodia on 11/11/15.
 */
public class Profile extends Controller {
	
	private static final Form<UserProfile> ProfileForm = Form.form(UserProfile.class);
		
    public Result viewProfile() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount getUser = UserAccount.getUser(user);

        JsonNode json = toJson(getUser);
        return ok(profile.render(json));
        //return ok(update_profile.render());
    }

    public Result updateProfile() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
		
		Form<UserProfile> form = ProfileForm.bindFromRequest();
        if (form.hasErrors()) { // Redirect with error
            return badRequest(update_profile.render(form));
        }

        return redirect(routes.Profile.viewProfile());
		//return ok(update_profile.render());
    }
	
	public UserProfile updateProfile(Form<UserProfile> profileForm){ 
		UserProfile profile = profileForm.get();
		String username = "Bailey";
		profile.username = username;
		ClassRecord course = new ClassRecord();
		course.classID = "COMS 4111";
		course.className = "Introduction to Databases";
		profile.classes = new ClassRecord[5];
		profile.classes[0] = course;
		System.out.println(profile.classes[0].classID);
		return profile;
	}
}
