package com.be.better.tactileboard;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageManager {

    private MqttAndroidClient client;
    final String Domain = "node02.myqtthub.com";
    final String ClientId = "Tactile-Board";
    final String Username = "suitceyes-admin";
    final String Password = "GirSjF17sPl%0gX&8aK3my00!";
    public MessageManager(Context context) {
        client = new MqttAndroidClient(context, Domain, ClientId);

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.w("MQTT", serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("MQTT", message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        Log.d("MQTT", "Connecting MQTT client...");
        connect();

    }

    private void connect(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setUserName(Username);
        options.setPassword(Password.toCharArray());

        try{
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Successfully connected to MQTT Server");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        }
        catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message) {

        try {
            MqttMessage msg = new MqttMessage();
            msg.setPayload(message.getBytes());
            client.publish("suitceyes/tactile-board/play", msg);
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
}
