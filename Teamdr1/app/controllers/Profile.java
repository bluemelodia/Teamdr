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
		//ClassRecord c = UserProfile.getClass(user);
        JsonNode user_json = toJson(getUser);
		JsonNode class_json = toJson(new ClassRecord("411", "DB"));
        return ok(profile.render(user_json, class_json));
        //return ok(update_profile.render());
    }

    public Result updateProfile() {
        String user = session("connected");
		System.out.println("Got here");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
		
		UserProfile p = UserProfile.getUser(user);
		Form<UserProfile> form = ProfileForm.bindFromRequest();
        if (form.hasErrors()) { // Redirect with error
            return badRequest(update_profile.render(form));
        }

		// convert HTML form to an Account model object, containing the params
        UserProfile prof = form.get();
		String e = form.data().get("email");
		String url = form.data().get("pic_url");
		String desc = form.data().get("description");
		p.email = e;
		p.pic_url = url;
		p.description = desc;
		System.out.println("Got here");
		prof.save();
		UserAccount getUser = UserAccount.getUser(user);
		JsonNode user_json = toJson(getUser);
		JsonNode class_json = toJson(new ClassRecord("411", "DB"));
        //return redirect(routes.Profile.viewProfile());
		return ok(profile.render(user_json, class_json));
    }
	
	/*public UserProfile updateProfile(Form<UserProfile> profileForm){ 
		UserProfile profile = profileForm.get();
		String username = "Bailey";
		profile.username = username;
		
		ClassRecord course = new ClassRecord("41111", "DB");
		profile.classes.add(course);
		System.out.println(profile.classes.get(0).classID);
		return profile;
	}*/
}
