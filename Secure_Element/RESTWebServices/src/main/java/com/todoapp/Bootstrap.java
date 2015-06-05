package com.todoapp;
 
import spark.Request;
import spark.Response;
import spark.Route;
 
import static spark.Spark.*;
 
public class Bootstrap {
 
    public static void main(String[] args) {
        get("/hello", new Route() {
            public Object handle(Request request, Response response) {
                return "Hello World!!";
            }
        });
    }
}