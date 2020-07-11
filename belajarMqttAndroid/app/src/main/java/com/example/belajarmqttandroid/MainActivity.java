package com.example.belajarmqttandroid;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    static String USERNAME = "";
    static String PASSWORD = "";
    static String topicStr = "pahe";

    MqttAndroidClient client;

    TextView subText;

    MqttConnectOptions options;

    Vibrator vibrator; //vibrator
    Ringtone myRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subText = findViewById(R.id. subText);

        vibrator= (Vibrator)getSystemService(VIBRATOR_SERVICE); //add vibrator

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
                        clientId);

        //mqtt user setup //uncomennt this if the MQTT broker using username and password
        //options = new MqttConnectOptions();
        //options.setUserName(USERNAME);
        //options.setPassword(PASSWORD.toCharArray());


        try {

            //connet to broker when the app started

            //IMqttToken token = client.connect(options); //uncomment this if if MQTT broker using username and password
            IMqttToken token = client.connect(); //comment this if mqtt broket using username and password
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
                    setSubscription(); //call the subscribe function
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure: connection timeout");
                    Toast.makeText(MainActivity.this, "connection fail", Toast.LENGTH_LONG).show();

                }
            });
        } catch (
                MqttException e) {
            e.printStackTrace();
        }


        //add callback function
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload())); //show the message to the text view

                vibrator.vibrate(500); //vibrate 500 millisecond when message arrive;
                myRingtone.play(); //play ringtone when message come
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void pub(View p){
        //publish method
        String topic = topicStr;
        String message = "Hello from the other side";
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(){
        try {
            client.subscribe(topicStr, 0);
        }catch (MqttException e){
            e.printStackTrace();;
        }
    }

    public void conn(View v){

        //connect to broker function, this function use by Button connect

        try {
            //IMqttToken token = client.connect(options); //uncomment this if if MQTT broker using username and password
            IMqttToken token = client.connect(); //comment this if mqtt broket using username and password
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
                    setSubscription(); //call the subscribe function
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure: connection timeout");
                    Toast.makeText(MainActivity.this, "connection fail", Toast.LENGTH_LONG).show();

                }
            });
        } catch (
                MqttException e) {
            e.printStackTrace();
        }

    }

    public  void disconn(View v){

        //disconnect to broker function //this function used by disconnect button

        try {

            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure: connection timeout");
                    Toast.makeText(MainActivity.this, "could not disconnected", Toast.LENGTH_LONG).show();

                }
            });
        } catch (
                MqttException e) {
            e.printStackTrace();
        }
    }

}