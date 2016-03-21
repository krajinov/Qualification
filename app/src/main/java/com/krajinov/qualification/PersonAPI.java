package com.krajinov.qualification;


import java.util.List;

import model.Person;
import retrofit.Callback;
import retrofit.http.GET;

public interface PersonAPI {

    @GET("/tasks/list.json")
    public void getFeed(Callback<List<Person>> response);
}
