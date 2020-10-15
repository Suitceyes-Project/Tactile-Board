package com.be.better.tactileboard.services;

import android.content.Context;
import android.util.Log;

import com.be.better.tactileboard.ServiceLocator;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Messenger implements IMessenger, ServiceLocator.IService {

    private MqttAndroidClient client;
    final String Domain = "tcp://node02.myqtthub.com:1883";
    final String ClientId = "Tactile-Board";
    final String Username = "suitceyes-admin";
    final String Password = "GirSjF17sPl%0gX&8aK3my00!";
    public Messenger(Context context) {
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
                    Log.d("MQTT", "Failed to connect to MQTT server");
                }
            });
        }
        catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void send(String topic, String message) {
        try {
            MqttMessage msg = new MqttMessage();
            msg.setPayload(message.getBytes());
            client.publish(topic, msg);
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
}
