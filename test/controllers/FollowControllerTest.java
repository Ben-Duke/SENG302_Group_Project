package controllers;

import accessors.UserAccessor;
import io.ebeaninternal.server.type.ScalarTypeJsonMapPostgres;
import junit.framework.TestCase;
import models.User;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.time.LocalDate;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class FollowControllerTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void followSuccess() {
        User userFollowing = UserAccessor.getById(1);
        Integer followingBefore = userFollowing.getFollowing().size();
        User userFollowed = UserAccessor.getById(2);
        Integer followersBefore = userFollowed.getFollowers().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/follow/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        User updatedUserFollowing = UserAccessor.getById(1);
        User updatedUserFollowed = UserAccessor.getById(2);

        assertEquals(followingBefore + 1, updatedUserFollowing.getFollowing().size());
        assertEquals(followersBefore + 1, updatedUserFollowed.getFollowers().size());
    }

    @Test
    public void followFailure() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/follow/600000").session("connected", "1");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());

    }

    @Test
    public void unfollowFailure() {
        User user1 = new User(
                "test1@test.com",
                "pass",
                "testuser1",
                "last",
                LocalDate.now(),
                "Male");

        User user2 = new User(
                "test2@test.com",
                "pass",
                "testuser2",
                "last",
                LocalDate.now(),
                "Male");



        UserAccessor.insert(user1);
        UserAccessor.insert(user2);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/unfollow/" + user2.getUserid()).session("connected", Integer.toString(user1.getUserid()));
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());

    }

    @Test
    public void unfollowSuccess() {
        User userFollowing = UserAccessor.getById(1);
        int followingBefore = userFollowing.getFollowing().size();
        User userFollowed = UserAccessor.getById(2);
        int followersBefore = userFollowed.getFollowers().size();
        Http.RequestBuilder setuprequest = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/follow/2").session("connected", "1");
        route(app, setuprequest);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/unfollow/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        User updatedUserFollowing = UserAccessor.getById(1);
        User updatedUserFollowed = UserAccessor.getById(2);

        assertEquals(followingBefore, updatedUserFollowing.getFollowing().size());
        assertEquals(followersBefore, updatedUserFollowed.getFollowers().size());
    }

    @Test
    public void testGetFirstTenFollowing(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowing/2?offset=0").session("connected", "1");
        Result result = route(app, request);
        int followingCount = (Json.parse (contentAsString(result)).size());
        TestCase.assertEquals(10,followingCount);
    }

    @Test
    public void testGetFollowingWithOffset(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowing/2?offset=10").session("connected", "1");
        Result result = route(app, request);
        int followingCount = (Json.parse (contentAsString(result)).size());
        TestCase.assertEquals(5,followingCount);
    }

    @Test
    public void testGetFollowingWithOffsetOutOfRange(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowing/6?offset=100").session("connected", "1");
        Result result = route(app, request);
        TestCase.assertEquals("Offset was larger than the amount of people following",contentAsString(result));
    }

    @Test
    public void testGetFirstTenFollowers(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowers/6?offset=0").session("connected", "1");
        Result result = route(app, request);
        int followingCount = (Json.parse (contentAsString(result)).size());
        TestCase.assertEquals(10,followingCount);
    }

    @Test
    public void testGetFollowersWithOffset(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowers/6?offset=10").session("connected", "1");
        Result result = route(app, request);
        int followingCount = (Json.parse (contentAsString(result)).size());
        TestCase.assertEquals(4,followingCount);
    }

    @Test
    public void testGetFollowersWithOffsetOutOfRange(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowers/6?offset=100").session("connected", "1");
        Result result = route(app, request);
        TestCase.assertEquals("Offset was larger than the amount of followers",contentAsString(result));
    }


    @Test
    public void testOutOfRangeUserIdFollowers(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowers/10000?offset=100").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void testOutOfRangeUserIdFollowing(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/getfollowing/10000?offset=100").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }
}
