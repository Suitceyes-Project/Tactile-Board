package com.be.better.tactileboard;

import ibt.ortc.extensibility.*;
import ibt.ortc.api.*;

public class MessageManager {

    private OrtcFactory factory;
    private OrtcClient client;
    private Ortc ortc;

    public MessageManager() {

        ortc = new Ortc();

        try {
            factory = ortc.loadOrtcFactory("IbtRealtimeSJ");
            client = factory.createClient();
            client.setClusterUrl("http://ortc-developers.realtime.co/server/2.1");
            // channel to ACI
            client.connect("Rb7lul", "testToken");

            client.onConnected = new OnConnected() {

                @Override
                public void run(OrtcClient sender) {
                    // Messaging client connected

                    // Now subscribe the channel
                    client.subscribe("TactileBoard", true,
                            new OnMessage() {
                                // This function is the message handler
                                // It will be invoked for each message received in channel TactileBoard

                                public void run(OrtcClient sender, String channel, String message) {
                                    // Received a message
                                    //---- I guess we want to trigger any display function or translation on the tactile board here ----
                                    System.out.println(message);
                                }
                            });
                }
            };

        } catch (Exception e) {
            System.out.println(String.format("Realtime Error: %s", e.toString()));
        }
    }

    public void sendMessage(String message) {

        final String msg = message;

        try {

            client.send("ACI_KYD", msg);
        } catch (Exception e) {
            System.out.println(String.format("Realtime Error: %s", e.toString()));
        }
    }

}
