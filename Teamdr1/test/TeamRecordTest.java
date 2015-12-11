import com.avaje.ebean.Ebean;
import models.UserAccount;
import org.junit.Test;
import play.test.WithApplication;

import static models.TeamRecord.createTeamRecord;
import static models.TeamRecord.exists;
import static org.junit.Assert.assertEquals;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class TeamRecordTest extends WithApplication {

    @Test
    public void simplerCheck() {
        int a = 1 * 1;
        assertEquals(1, a);
    }

    @Test
    public void testCreateTeamRecord() {
        String tid = "testTeam15";
        String teamName = "testTeamName";
        String thisClass = "COMS4118";
        UserAccount user = new UserAccount();
        user.username = "testUser15";
        Ebean.save(user);

        models.TeamRecord team;
        team = createTeamRecord(tid, user, teamName, thisClass);
        Ebean.save(team);
        assert(exists(tid));
        Ebean.delete(user);
        Ebean.delete(team);
    }

   @Test
    public void testGetTeam() {
        String tid = "testTeam20";
        String teamName = "testTeamName";
        String thisClass = "COMS4118";
        UserAccount user = new UserAccount();
        user.username = "testUser20";
        Ebean.save(user);
        models.TeamRecord team =createTeamRecord(tid, user, teamName, thisClass);
        Ebean.save(team);
        assertEquals(models.TeamRecord.getTeam(tid).tid, tid);
        Ebean.delete(user);
        Ebean.delete(team);
    }

    @Test
    public void testGetTeamForClass() {

        UserAccount user = new UserAccount();
        user.username = "testUser10";
        Ebean.save(user);
        models.TeamRecord team = createTeamRecord("teamID10", user, "teamName", "COMS4118");
        Ebean.save(team);
        assertEquals(models.TeamRecord.getTeamForClass(user.username, "COMS4118").tid, "teamID10");
        Ebean.delete(user);
        Ebean.delete(team);
    }

  //  @Test
  //  public void

}
