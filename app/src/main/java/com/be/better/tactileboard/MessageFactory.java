package com.be.better.tactileboard;

import com.google.gson.Gson;

public class MessageFactory
{
    private static Gson gson = new Gson();

    public static String create(String functionName, Object payload)
    {
        /*
        Message msg = new Message();
        msg.FunctionName = functionName;
        msg.Payload = payload;
        */
        return gson.toJson(payload);
    }
}
