package com.coms309.drinkerschoice;

public class Const {
    public static final String SERVER_URL = "http://proj309-ds-03.misc.iastate.edu:8080";

    public static final String REQUEST_ALL_USERS =  "/request/users";
    public static final String REQUEST_USER_BY_ID = "/request/user/";                               //append id
    public static final String REQUEST_BUSINESS_USER = "/request/business/";                        //append id
    public static final String IS_USERNAME_TAKEN =  "/request/exists/";                             //append username
    public static final String REQUEST_ALL_POSTS =  "/request/posts";
    public static final String REQUEST_POST_BY_ID = "/request/post/";                               //append id
    public static final String REQUEST_NUM_POSTS =  "/request/posts/count";
    public static final String REQUEST_COMMENTS =  "/request/comments/";                            //append id


    public static final String CREATE_NEW_ACCOUNT = "/post/new/user";
    public static final String UPDATE_USER_PASS =   "/post/update/password/";                       //append id
    public static final String UPDATE_BUSINESS_PASS = "/put/update/business/password/";             //append id
    public static final String UPDATE_BUSINESS =    "/put/update/business/";
    public static final String CREATE_NEW_BUSINESS = "/post/new/business";
    public static final String DELETE_USER_BY_ID =  "/post/delete/user/";                           //append id
    public static final String UPDATE_USER_BY_ID =  "/post/update/user/";                           //append id;
    public static final String IMAGE =              "/post/new/image";
    public static final String LOGIN_USER =         "/post/user/login";
    public static final String BUSINESS_LOGIN =     "/post/business/login";
    public static final String MAKE_NEW_POST =      "/post/new/post";
    public static final String SEND_COMMENT =       "/post/comment";
    public static final String SEND_RATING =        "/post/new/rating";

    public static final String WEBSOCKET =          "/websocket/";

    public static final String DELETE_RATING =      "/delete/rating";

    public static final String REQUEST_RIDE_REQUEST_BY_ID = "/request/driving/";                    //append id
    public static final String REQUEST_ALL_RIDE_REQUESTS = "/request/driving/all";
    public static final String REMOVE_RIDE_REQUEST_BY_ID = "/delete/driving/";				        //append id
    public static final String POST_NEW_RIDE_REQUEST = "/post/new/driving";
    public static final String UPDATE_DRIVING_REQUEST_BY_ID = "/post/update/driving/";				//append id
    public static final String ACCEPT_RIDE_REQUEST_BY_ID = "/accepted/driving/";                    //append id

    public static final String POST_NEW_BUSINESS_POST = "/post/new/business/post";
    public static final String POST_BUSINESS_RATING =   "/post/new/business/rating";
    public static final String REQUEST_ALL_BUSINESS_POSTS = "/request/business/posts/all";
    public static final String REQUEST_BUSINESS_POST_BY_ID = "/request/business/post/";             //append id
    public static final String DELETE_BUSINESS_POST_BY_ID = "/delete/business/post/";               //append id
    public static final String REQUEST_ALL_BUSINESS_POSTS_BY_POSTER_ID = "/request/business/posts/";//append id

    public static final String REQUEST_ACCEPTED_DRIVING_BY_ID = "/request/accepted/driving/";       //append username
    public static final String GET_MY_ACCEPTED_RIDES_ENDPOINT = "/request/accepted/";               //append username



    //Strings below are used for debugging
    public static final String SAMMY_DEBUG = "526752675267";

    public static final String YODEBUG = "804480448044";

}
