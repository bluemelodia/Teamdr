package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.errorPage;
import views.html.profile;
import views.html.update_profile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;

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
        String announcement = "";
        return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user), announcement));
    }

    public Result showUpdateProfilePage() {
        String user = session("connected");
        UserProfile profile = UserProfile.getUser(user);
        return ok(update_profile.render(profile));
    }

    public Result updateProfile() {
        String user = session("connected");
		System.out.println("IN UPDATE PROFILE");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }

        JsonNode json = request().body().asJson();
        String description = json.get("description").toString().replaceAll("[^A-Za-z0-9$-.+!*(), ]", "");
        String picture = json.get("picture").toString().replaceAll("[^A-Za-z0-9$-_.+!*'(),]", "");
        String email = json.get("email").toString().replaceAll("[^A-Za-z0-9@!#$%&'*+-/=?^_`{|}~]", "");
        System.out.println("description: " + description + " picture: " + picture + " email: " + email);

        UserProfile p = UserProfile.getUser(user);
        System.out.println(p.email + " " + p.pic_url + " " + p.description);

        // set to the new values
        p.description = description;
        p.pic_url = picture;
        // if the url doesn't work, prevent the user from making the change
        if (picture.length() > 0) {
            try {
                URL url = new URL(picture);
                URLConnection conn = url.openConnection();
                conn.connect();
            } catch (MalformedURLException e) {
                p.pic_url = "";
                System.out.println("MALFORMED URL EXCEPTION");
                return badRequest(toJson("You provided an invalid photo URL."));
            } catch (IOException e) {
                p.pic_url = "";
                System.out.println("IO EXCEPTION");
                return badRequest(toJson("You provided an invalid photo URL."));
            }
        }

        // if the email is invalid, prevent the user from making the change
        // Source: http://www.tutorialspoint.com/javaexamples/regular_email.htm
        p.email = email;
        System.out.println("email length: " + email.trim().length());
        if (email.trim().length() > 0) {
            String emailCheck = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            Boolean test = email.matches(emailCheck);
            if (!test) {
                return badRequest(toJson("You provided an invalid email."));
            }
        }
        p.save();
        UserAccount getUser = UserAccount.getUser(user);
        String announcement = "Updated profile.";
        return ok(toJson("Accepted"));
    }

    public Result viewNotifications() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount thisUser = UserAccount.getUser(user);

        List<Notification> allNotifs = Notification.getNotifs(thisUser.username);
        String notifsJson = toJson(allNotifs).toString();
        return ok(views.html.notifications.render(notifsJson));
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

        if (!Notification.notifExists(notificationID)) {
            return ok(toJson("Accepted"));
        }

        // Find that notification, getting the classID and teamID of the requester
        Notification thisNotif = Notification.getThisNotif(notificationID);
        String classID = thisNotif.classID;
        String teamID = thisNotif.teamID;
        if (!TeamRecord.exists(teamID)) { // someone already swiped right, or team was otherwise purged
            Notification.deleteNotif(notificationID);
            return ok(toJson("Accepted"));
        }

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
        Notification.deleteNotif(notificationID);

        return ok(toJson("Accepted"));
    }

    // Just delete the record
    public Result rejectNotification() {
        JsonNode json = request().body().asJson();
        System.out.println("REJECT");
        int notificationID = json.asInt();
        if (!Notification.notifExists(notificationID)) {
            return ok(toJson("Rejected"));
        }
        System.out.println("Deleting this notif: " + notificationID);
        // Delete this notification
        Notification.deleteNotif(notificationID);

        return ok(toJson("Rejected"));
    }
	
	public Result addClass() {
		String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount userAccount = UserAccount.getUser(user);
		UserProfile p = UserProfile.getUser(user);

        boolean foundClass = false;
		final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String classID = values.get("classID")[0];
        System.out.println("ClassID to find: " + classID);
        String className = null;

        List<ClassRecord> cs = ClassRecord.findAll();
        for (int i = 0; i < cs.size(); i++) {
            System.out.println("DATABASE CLASS: " + cs.get(i).classID + " : " + cs.get(i).className);
        }

        ClassRecord c = null;
        int i;
        for(i=0; i<cs.size(); i++){
            c =  cs.get(i);
            if(c.classID.equals(classID)) {
                className = c.className;
                foundClass = true;
                break;
            }
        }

        ArrayList<ClassRecord> classes = new ArrayList<ClassRecord>();
		//String className = values.get("className")[0];
		if (foundClass) {
            userAccount.addClass(classID);
            System.out.println("ADDED CLASS: " + classID + " CLASS NAME: " + className);
            for (String userClass: userAccount.getClassList()) {
                ClassRecord thisClass = ClassRecord.getClass(userClass);
                classes.add(thisClass);
            }
        } else {
            JsonNode errorJson = toJson("The class you tried to add does not exist.");
            return ok(errorPage.render(errorJson));
        }
        String announcement = "You have added " + classID;
        return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user), announcement));
    }
}