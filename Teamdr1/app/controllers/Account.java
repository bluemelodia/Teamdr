package controllers;
import models.UserAccount;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import views.html.*;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    // Enables passing of params into the form
    private static final Form<UserAccount> AccountForm = Form.form(UserAccount.class);
    public Result createUser() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<UserAccount> form = AccountForm.bindFromRequest();
        // convert HTML form to an Account model object, containing the params
        UserAccount account = form.get();
        // save the data sent through HTTP POST
        account.save();
        return redirect(routes.Profile.viewProfile());
    }

    public Result newAccount() {
        return ok(account.render(AccountForm));
    }

    public Result signIn() {
        return TODO;
    }
}