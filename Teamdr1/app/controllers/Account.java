package controllers;
import models.UserAccount;
import models.UserProfile;
import models.ClassRecord;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import play.libs.Json;
import views.html.*;
import java.util.*;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    // Enables passing of params into the form
    private static final Form<UserAccount> AccountForm = Form.form(UserAccount.class);
    private static final Form<UserAccount> LoginForm = Form.form(UserAccount.class);
    private static final Form<UserAccount> ForgotForm = Form.form(UserAccount.class);
	private static final Form<UserProfile> ProfileForm = Form.form(UserProfile.class);

    public Result createUser() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<UserAccount> form = AccountForm.bindFromRequest();
		
        if (form.hasErrors()) { // Redirect with error
            return badRequest(account.render(form));
        }
        // convert HTML form to an Account model object, containing the params
        UserAccount newAccount = form.get();
		String username = form.data().get("username");
		String email = form.data().get("email");
		
        if (UserAccount.exists(newAccount.username)) {
            form.reject("username", "User already exists.");
            return badRequest(account.render(form));
        }
		
		if (username.isEmpty()) {
            form.reject("username", "Must provide username.");
            return badRequest(account.render(form));
        }

        for (int i = 0; i < username.length(); i++) {
            if (!Character.isLetterOrDigit(username.charAt(i))) {
                form.reject("username", "Alphanumeric characters only.");
                return badRequest(account.render(form));
            }
        }

        // save the data sent through HTTP POST
		UserProfile newProfile = new UserProfile();
        String startingDescription = "I love CS!";
		newProfile.updateProfile(username, email, startingDescription);
		newProfile.save();
		newAccount.profile = newProfile;
		newAccount.addProfile(username, email);
		System.out.println(newProfile.email);
		//newAccount.updateProfile(username);
		newAccount.save();
		
        session("connected", newAccount.username);
        return redirect(routes.Profile.viewProfile());
    }

    // Get the signup form
    public Result signUp() {
        // Pre-populate the classes database if there are no classes available
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
        return ok(account.render(AccountForm));
    }

    public Result signIn() {
        return ok(login.render(LoginForm));
    }

    // Validate the user's credentials
    public Result authenticateUser() {
        Form<UserAccount> form = LoginForm.bindFromRequest();
        if (form.hasErrors()) { // Redirect with error
			System.out.println("Error.");
            return badRequest(login.render(form));
        }
        UserAccount getAccount = form.get();
        if (!UserAccount.exists(getAccount.username)) {
            form.reject("username", "User does not exist.");
            return badRequest(login.render(form));
        }
        UserAccount getUser = UserAccount.getUser(getAccount.username);
        if (!getUser.password.equals(getAccount.password)) {
            form.reject("password", "Incorrect password.");
            return badRequest(login.render(form));
        }
        // This stores info about the user's session
        // Other classes can fetch the username from here
        session("connected", getAccount.username);
        return redirect(routes.Profile.viewProfile());
    }

    public Result logoutUser() {
        session().remove("connected");
        return redirect(routes.Account.signIn());
    }
}