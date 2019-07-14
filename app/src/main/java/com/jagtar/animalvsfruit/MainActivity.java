package com.jagtar.animalvsfruit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {


    String[] animal = {"ਬਾਂਦਰ", "ਘੋੜਾ", "ਹਿਰਨ" ,"ਭਾਲੂ", "ਗਾਂ", "ਬਿੱਲੀ"};
    String[] fruit = {"ਅਮਰੂਦ", "ਅਨਾਨਾਸ" ,"ਸੇਬ", "ਤਰਬੂਜ", "ਅੰਬ", "ਅੰਗੂਰ"};
    String displayWord = "";
    int selectArray;
    int selectIndex;
    TextView question;
    TextView warning;
    TextView showScore;
    String dataFromParticle = "";
    String particleId = "";
    String currentWord;
    boolean isVotingDone = false;
    private final String TAG = "Alpha";
    Random r = new Random();
    // MARK: Particle Account Info
    private final String PARTICLE_USERNAME = "jsk5755@gmail.com";
    private final String PARTICLE_PASSWORD = "Alpha123";

    // MARK: Particle Publish / Subscribe variables
    private long subscriptionId;

    // MARK: Particle device
    private List<ParticleDevice> mDevice;
    private List<DevicesData> devices = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = (TextView) findViewById(R.id.questionLabel);
        warning = (TextView) findViewById(R.id.warning);
        showScore = (TextView) findViewById(R.id.showScore);
        // 1. Initialize your connection to the Particle API
        ParticleCloudSDK.init(this.getApplicationContext());

        // 2. Setup your device variable
        getDeviceFromCloud();
        setWord();
    }



    public void setWord(){
        Random r1 = new Random();
        //selecting one array from two
        selectArray = r1.nextInt(2);
        //selecting a random word from array
        selectIndex = r1.nextInt(6);
        if(selectArray == 0){
            question.setText(animal[selectIndex]);
            displayWord = animal[selectIndex];
            //storing current word index
            currentWord = "1";
        }else{
            question.setText(fruit[selectArray]);
            displayWord = fruit[selectIndex];
            currentWord = "2";
        }
    }

    //button to show next question
    public void nextQuestion(View view) {
        //getting answers from particle
        getData();
        //if all particles answers only then show the next question otherwise show warning
        if(allDevicesAnswered() == false)
        {
            warning.setText("didn't get answer from all devices");
        }
        else if(allDevicesAnswered() == true){

            warning.setText("");
            //checking the answers and storing the scores
            for (int i = 0; i < devices.size(); i++)
            {

                if(devices.get(i).getVote().equals(currentWord)){
                    //sending command, function name and current device in parameters
                        sendData("green","colors",i);

                        //modifying scores
                        devices.get(i).setScore(devices.get(i).getScore() + 1);
                        showScore.setText("Score " + devices.get(i).getScore());
                        Log.d("socre", "device " + i + devices.get(i).getScore());

                }
                else{
                    //sending command, function name and current device in parameters
                    sendData("red","colors",i);

                    //modifying scores
                    devices.get(i).setScore(devices.get(i).getScore() + 1);
                    showScore.setText("Score " + devices.get(i).getScore());
                    Log.d("score", "device" + i + devices.get(i).getScore());

                }
            }
            setWord();
        }

    }

    //checking if all devices has voted
    public boolean allDevicesAnswered(){
        for (int i = 0; i < devices.size(); i++)
        {
            if(devices.get(i).getHasVoted() == false) {
                isVotingDone = false;
            }
            else {
                isVotingDone = true;
            }
        }
        return  isVotingDone;
    }


    //calling this function to send data to devices
    public void sendData(String commandToSend,String funName,Integer currentdevice) {
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                // 2. build a list and put the r,g,b into the list
                List<String> functionParameters = new ArrayList<String>();
                functionParameters.add(commandToSend);

              //  for (int i = 0; i < devices.size(); i++) {
                    try {
                        devices.get(currentdevice).getDevice().callFunction(funName, functionParameters);
                        //mDevice.callFunction("colors", functionParameters);
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                    }
               // }
                return -1;
            }

            @Override
            public void onSuccess(Object o) {

                Log.d(TAG, "Sent colors command to device.");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    //calling this function to get data from devices
    public void getData(){
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToAllEvents(
                        "broadcast",  // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                dataFromParticle = "" + event.dataPayload;
                                particleId = "" + event.deviceId;
                                //for each device, checking if the device has pressed the button
                                if(!dataFromParticle.equals("3")){
                                for (int i = 0; i<devices.size();i++){
                                    if(devices.get(i).getDevice().getID().equals(particleId)){
                                        devices.get(i).setHasVoted(true);
                                        devices.get(i).setVote(dataFromParticle);
                                    }
                                }}
                                else if(dataFromParticle.equals("3")){

                                }

                            }

                            public void onEventError(Exception e) {
                                Log.e(TAG, "Event error: ", e);
                            }
                        });
                return -1;
            }
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully subscribed device to Cloud");
                Log.d("vote", devices.get(0).getVote());
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    public void getDeviceFromCloud() {
        // This function runs in the background
        // It tries to connect to the Particle Cloud and get your device
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(PARTICLE_USERNAME, PARTICLE_PASSWORD);
                //mDevice = particleCloud.getDevice(DEVICE_ID);
                mDevice = particleCloud.getDevices();
                for (int i = 0; i<mDevice.size();i++){
                    devices.add(new DevicesData(mDevice.get(i)));

                    //Log.d("jenelle",mDevice.get(i).getID());
                }

                for (int i = 0; i<devices.size();i++){
                    Log.d("id particle", "Hello World!!! " + devices.get(i).getDevice().getID());
                }
                return -1;
            }

            @Override
            public void onSuccess(Object o)
            {
                Log.d(TAG, "Successfully got device from Cloud");
                getData();
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }
}
