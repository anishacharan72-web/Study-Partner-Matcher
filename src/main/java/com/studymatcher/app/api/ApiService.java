package com.studymatcher.app.api;

import com.studymatcher.app.model.Match;
import com.studymatcher.app.model.Rating;
import com.studymatcher.app.model.Session;
import com.studymatcher.app.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit interface for the Study Matcher Spring Boot REST API.
 * Base URL: configured in ApiClient.
 */
public interface ApiService {

    // ===== Profile =====
    @POST("api/v1/profile")
    Call<User> createProfile(@Body User user);

    @PUT("api/v1/profile/{id}")
    Call<User> updateProfile(@Path("id") String userId, @Body User user);

    @GET("api/v1/profile/{id}")
    Call<User> getProfile(@Path("id") String userId);

    // ===== Subjects =====
    @POST("api/v1/subjects")
    Call<Void> saveSubjects(@Path("id") String userId, @Body List<String> subjects);

    @GET("api/v1/subjects/{userId}")
    Call<List<String>> getSubjects(@Path("userId") String userId);

    // ===== Availability =====
    @POST("api/v1/availability")
    Call<Void> saveAvailability(@Body AvailabilityRequest request);

    @GET("api/v1/availability/{userId}")
    Call<List<AvailabilitySlot>> getAvailability(@Path("userId") String userId);

    // ===== Matches =====
    @GET("api/v1/matches/{userId}")
    Call<List<Match>> getMatches(@Path("userId") String userId);

    @PUT("api/v1/matches/{matchId}/accept")
    Call<Void> acceptMatch(@Path("matchId") String matchId);

    @PUT("api/v1/matches/{matchId}/decline")
    Call<Void> declineMatch(@Path("matchId") String matchId);

    // ===== Ratings =====
    @POST("api/v1/ratings")
    Call<Void> submitRating(@Body Rating rating);

    @GET("api/v1/ratings/{userId}")
    Call<List<Rating>> getRatings(@Path("userId") String userId);

    // ===== Sessions =====
    @POST("api/v1/sessions")
    Call<Session> createSession(@Body Session session);

    @GET("api/v1/sessions/{userId}")
    Call<List<Session>> getSessions(@Path("userId") String userId);

    @PUT("api/v1/sessions/{sessionId}/complete")
    Call<Void> completeSession(@Path("sessionId") String sessionId);

    // ===== Photo =====
    @PUT("api/v1/profile/{id}/photo")
    Call<Void> updatePhoto(@Path("id") String userId, @Body PhotoRequest request);

    // ===== Inner request DTOs =====
    class AvailabilityRequest {
        public String userId;
        public List<AvailabilitySlot> slots;
    }

    class AvailabilitySlot {
        public String dayOfWeek;   // MON, TUE, WED, THU, FRI, SAT, SUN
        public String slotStart;   // HH:mm
        public String slotEnd;     // HH:mm
    }

    class PhotoRequest {
        public String photoUrl;
    }
}
