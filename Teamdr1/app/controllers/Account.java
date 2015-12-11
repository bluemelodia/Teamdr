package controllers;
import com.fasterxml.jackson.databind.JsonNode;
import models.UserAccount;
import models.UserProfile;
import models.ClassRecord;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import play.libs.Json;
import views.html.*;
import java.util.*;
import java.util.stream.Collector;

import static play.libs.Json.toJson;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    // Enables passing of params into the form
    private static final Form<UserAccount> AccountForm = Form.form(UserAccount.class);
    //private static final Form<UserAccount> LoginForm = Form.form(UserAccount.class);
    private static final Form<UserAccount> ForgotForm = Form.form(UserAccount.class);
	private static final Form<UserProfile> ProfileForm = Form.form(UserProfile.class);

    public Result createUser() {
        JsonNode json = request().body().asJson();
        String username = json.get("username").toString().replaceAll("[^A-Za-z0-9]", "");
        String password = json.get("password").toString().replaceAll("[^A-Za-z0-9]", "");
        String email = json.get("email").toString().replaceAll("[^A-Za-z0-9@.]", "");

        if (username.length() < 1 || password.length() < 1) {
            System.out.println("Failed the username/password check.");
            return badRequest(toJson("You provided an invalid username or password."));
        } if (email.length() < 1) {
            email = "";
        }
        if (UserAccount.exists(username)) {
            System.out.println("Failed the exists check");
            return badRequest(toJson("The username you provided already exists."));
        }

        // save the data sent through HTTP POST
        UserAccount newAccount = new UserAccount();
        newAccount.username = username;
        newAccount.password = password;
		UserProfile newProfile = new UserProfile();
        String startingDescription = "I love CS!";
		newProfile.updateProfile(username, email, startingDescription);
		newProfile.save();
		newAccount.profile = newProfile;
		newAccount.addProfile(username, email);
		System.out.println(newProfile.email);
		//newAccount.updateProfile(username);
		newAccount.save();
		System.out.println("success");
        session("connected", newAccount.username);
        return ok(toJson("Accepted"));
    }

    // Get the signup form
    public Result signUp() {
        // Pre-populate the classes database if there are no classes available
        System.out.println("SIGNING UP");

        int numberOfClasses = ClassRecord.getNumClasses();
        if (numberOfClasses < 1) {
            ClassRecord.createNewClass("COMS4111", "Intro to Databases");
            ClassRecord.createNewClass("COMS4118", "Operating Systems");
            ClassRecord.createNewClass("COMS4156", "Advanced Software Engineering");
            ClassRecord.createNewClass("COMS4115", "Programming Languages and Translators");
            ClassRecord.createNewClass("COMS3157", "Advanced Programming");
            ClassRecord.createNewClass("COMS6111", "Advanced Databases");
            ClassRecord.createNewClass("COMS4112", "Database Systems Implementation");
            ClassRecord.createNewClass("COMS4119", "Computer Networks");
            ClassRecord.createNewClass("COMS6998-7", "Micro-Service Apps and APIs");
            ClassRecord.createNewClass("COMS6156", "Topics in Software Engineering");
        }
        List<ClassRecord> allClasses = ClassRecord.findAll();
        for (ClassRecord classRecord: allClasses) {
            System.out.println("ADDED CLASS: " + classRecord.className + " " + classRecord.classID);
        }
        return ok(account.render());
    }

    public Result signIn() {
        String message1 = "";
        String message2 = "";
        return ok(login.render());
    }

    // Validate the user's credentials
    public Result authenticateUser() {
        JsonNode json = request().body().asJson();
        // sanitize the input, thus preventing SQL injections
        String username = json.get("username").toString().replaceAll("[^A-Za-z0-9]", "");
        String password = json.get("password").toString().replaceAll("[^A-Za-z0-9]", "");
        System.out.println("ABOUT TO GO");
        if (username.length() < 1 || password.length() < 1) {
            return badRequest(toJson("You provided an invalid username or password."));
        }
        if (!UserAccount.exists(username)) {
            System.out.println("NOT EXIST!");
            String message1 = "The user " + username + " does not exist.";
            String message2 = "";
            return badRequest("The username you provided does not exist.");
        }
        UserAccount getUser = UserAccount.getUser(username);
        if (!getUser.password.equals(password)) {
            return badRequest("You provided the wrong password for this username.");
        }
        // This stores info about the user's session
        // Other classes can fetch the username from here
        session("connected", username);
        return ok(toJson("Accepted"));
    }

    public Result logoutUser() {
        session().remove("connected");
        return redirect(routes.Account.signIn());
    }
}