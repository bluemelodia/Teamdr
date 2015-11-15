package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Notifications;
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

        // If the user has notifications, show them
        String notifs = "Hi";
        if (Notifications.hasNotifs(getUser.username)) {
            notifs = "You have notifications: ";
        }

        JsonNode user_json = toJson(getUser);
		JsonNode class_json = toJson(new ClassRecord("411", "DB"));
        JsonNode profile_json = toJson(UserProfile.getUser(getUser.username).description);
        JsonNode notifs_json = toJson(notifs);
        return ok(profile.render(user_json, class_json, profile_json, notifs_json));
        //return ok(update_profile.render());
    }

    public Result showUpdateProfilePage() {
        return ok(update_profile.render(ProfileForm));
    }

    public Result updateProfile() {
        String user = session("connected");
		System.out.println("Got here");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
		
		UserProfile p = UserProfile.getUser(user);
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String email = values.get("email")[0];
        String pictureURL = values.get("pictureURL")[0];
        String description = values.get("description")[0];

		p.email = email;
		p.pic_url = pictureURL;
		p.description = description;
		System.out.println("Also here");
		p.save();

        UserAccount getUser = UserAccount.getUser(user);

        // If the user has notifications, show them
        String notifs = "Hi";
        if (Notifications.hasNotifs(getUser.username)) {
            notifs = "You have notifications: ";
        }

		JsonNode user_json = toJson(getUser);
        // TODO: implement add user to class


		JsonNode class_json = toJson(new ClassRecord("411", "DB"));
        JsonNode profile_json = toJson(p.description);
        JsonNode notifs_json = toJson(notifs);
        //return redirect(routes.Profile.viewProfile());

        System.out.println("RENDERING");
		return ok(profile.render(user_json, class_json, profile_json, notifs_json));
    }

    public Result viewNotifications() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount thisUser = UserAccount.getUser(user);

        List<Notifications> allNotifs = Notifications.getNotifs(thisUser.username);
        return ok(notifs.render(allNotifs));
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
