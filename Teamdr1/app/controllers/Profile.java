package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.nashorn.internal.ir.ObjectNode;
import models.*;
import play.api.libs.json.JsPath;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.profile;
import views.html.update_profile;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static play.libs.Json.toJson;

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
        String notifs = "You have no notifications.";
        if (Notifications.hasNotifs(getUser.username)) {
            notifs = "You have " + Notifications.countNotifs(getUser.username) + " notification(s)";
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
        String notifs = "You have no notifications.";
        if (Notifications.hasNotifs(getUser.username)) {
            notifs = "You have " + Notifications.countNotifs(getUser.username) + " notification(s)";
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
        return ok(toJson(allNotifs));
    }

    public Result showNotifications() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        return TODO;
    }

    // This one only called in type 1 call
    public Result acceptNotification() {
        JsonNode json = request().body().asJson();
        int notificationID = json.asInt();
        System.out.println(notificationID);

        if (!Notifications.notifExists(notificationID)) {
            return ok(toJson("Accepted"));
        }

        // Find that notification, getting the classID and teamID of the requester
        Notifications thisNotif = Notifications.getThisNotif(notificationID);
        String classID = thisNotif.classID;
        String teamID = thisNotif.teamID;

        // Get the requester's team members
        TeamRecord requesterTeam = TeamRecord.getTeam(teamID);
        String[] theirMembers = (requesterTeam.teamMembers).split(" ");
        ArrayList<String> theirTeam = new ArrayList<String>();
        for (int j = 0; j < theirMembers.length; j++) {
            theirTeam.add(theirMembers[j].trim());
        }

        // Get yourself
        String user = session("connected");

        // Do the merge only if you aren't already on the same team as the requester
        TeamRecord myTeam = TeamRecord.getTeamForClass(user, classID);
        Boolean sameTeam = false;
        String[] teamMembers = (myTeam.teamMembers).split(" ");
        for (int i = 0; i < teamMembers.length; i++) {
            String currentMember = teamMembers[i].trim();
            if (theirTeam.contains(currentMember)) {
                sameTeam = true; // The team was already merged, do not try to merge again!
            }
        }

        if (!sameTeam) { // can do the merge
            System.out.println("gonna merge");
            requesterTeam = requesterTeam.updateTeam(requesterTeam.tid, myTeam.tid, classID);
            requesterTeam.save();
            //td = td.updateTeam(thisTeam, user);
            //System.out.println("new team " + td.teamMembers);
            //td.save();
        }

        // Delete the notification
        Notifications.deleteNotif(notificationID);

        // TODO: if the recipient swipes right through the team search page, also merge and delete the notif
        // TODO: send a notification to the entire new team, saying that the team was merged.
        // TODO: make sure two people cannot swipe left on the same person (to oust them) - if they have been already ousted, the second person's swipe does nothing
        return ok(toJson("Accepted"));
    }

    // Just delete the record
    public Result rejectNotification() {
        JsonNode json = request().body().asJson();
        int notificationID = json.asInt();
        // Delete this notification
        Notifications.deleteNotif(notificationID);

        return ok(toJson("Rejected"));
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
