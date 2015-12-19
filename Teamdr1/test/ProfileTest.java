import com.fasterxml.jackson.databind.JsonNode;
import controllers.Profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import models.Notification;
import models.UserAccount;
import models.UserProfile;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.api.libs.json.JsValue;
import play.api.libs.Jsonp;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;
import play.twirl.api.Content;
import play.test.FakeRequest;

@RunWith(PowerMockRunner.class)
//@PowerMockIgnore("javax.crypto.*")
@PowerMockIgnore({"javax.management.*", "javax.crypto.*"})
class ProfileTest extends WithApplication {



    @PrepareForTest({ UserProfile.class,UserAccount.class, Notification.class })
    @Test
    public void testUpdateProfile() {


        UserProfile userProfileMock = Mockito.mock(UserProfile.class);
        userProfileMock.email = "nitesh.chauhan@columbia.edu";
        userProfileMock.description = "xyz";
        userProfileMock.pic_url = "http://www.columbia.edu";
        Mockito.doNothing().when(userProfileMock).save();

        mockStatic(UserProfile.class);
        when(UserProfile.getUser(Mockito.anyString())).thenReturn(userProfileMock);


        mockStatic(UserAccount.class);
        when(UserAccount.getUser(Mockito.anyString())).thenReturn(new UserAccount());

        mockStatic(Notification.class);
        when(Notification.getNotifs(Mockito.anyString())).thenReturn(new ArrayList<Notification>());

        /*RequestBuilder request = new RequestBuilder();
        request.method(GET);
        request.uri("/profile");
        request.session("connected","w");*/
        //JsonNode jsonode = Json.parse("{\"firstName\":\"Foo\", \"lastName\":\"Bar\", \"age\":13}");
        JsonNode jsonode = Json.parse("{\"description\":\"abc\",\"picture\":\"http://facebook.com\",\"email\":\"nc2663@columbia.edu\"}");


        RequestBuilder request = new RequestBuilder()
                .method(POST).bodyJson(jsonode)
                .uri("/update").session("connected","w");

        Result result = route(request);

        assertEquals(200, result.status());


    }

}
