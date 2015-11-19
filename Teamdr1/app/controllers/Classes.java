package controllers;

import models.Notification;
import models.UserAccount;
import models.UserProfile;
import play.mvc.Controller;
import play.data.Form;
import play.mvc.Result;
import models.ClassRecord;
import views.html.*;
import java.util.List;

/**
 * Created by bluemelodia on 11/12/15.
 */

public class Classes extends Controller {
    private static final Form<ClassRecord> ClassForm = Form.form(ClassRecord.class);

    public Result leaveClass(String classId) {
        String user = session("connected");
        return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
    }

    public Result retrieveClass() {
        return ok(classes.render(ClassForm));
    }

    public Result putClass() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<ClassRecord> form = ClassForm.bindFromRequest();
        ClassRecord newClass = form.get();
        // save the data sent through HTTP POST
        System.out.println("newClass: " + newClass.classID + " " + newClass.className);
        newClass.save();

        List<ClassRecord> classes = ClassRecord.findAll();
        for (int i = 0; i < classes.size(); i++) {
            System.out.println(classes.get(i).classID + " " + classes.get(i).className);
        }

        return redirect(routes.Classes.retrieveClass());
    }
}
