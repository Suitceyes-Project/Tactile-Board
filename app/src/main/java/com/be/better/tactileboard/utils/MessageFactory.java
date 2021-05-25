package com.be.better.tactileboard.utils;

import com.be.better.tactileboard.models.OntologyMessage;
import com.google.gson.Gson;

public class MessageFactory
{
    private static Gson gson = new Gson();

    public static String create(Object payload)
    {
        return gson.toJson(payload);
    }

    public static String createOntologyMessage(String message)
    {
        OntologyMessage msg = new OntologyMessage();
        msg.message = message;
        return gson.toJson(msg);
    }
}
