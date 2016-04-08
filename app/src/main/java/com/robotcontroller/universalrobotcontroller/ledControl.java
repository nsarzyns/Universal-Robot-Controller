package com.robotcontroller.universalrobotcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ledControl extends AppCompatActivity {

    Button ledOn, ledOff, bluetoothDisconnect;
    SeekBar brightnessAmountBlue;
    SeekBar brightnessAmountGreen;
    SeekBar brightnessAmountRed;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter BTAdapter = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    TextView brightLevelBlue;
    TextView brightLevelGreen;
    TextView brightLevelRed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(BluetoothPair.EXTRA_ADDRESS);

//view of the ledControl layout
        setContentView(R.layout.activity_led_control);
//call the widgtes
        ledOn = (Button) findViewById(R.id.ledON);
        ledOff = (Button) findViewById(R.id.ledOff);
        bluetoothDisconnect = (Button) findViewById(R.id.bluetoothDisconnect);
        //Initilize Blue Led
        brightnessAmountBlue = (SeekBar) findViewById(R.id.brightnessAmountBlue);
        brightLevelBlue = (TextView) findViewById(R.id.brightLevelBlue);
        //Initilize Green Led
        brightnessAmountGreen = (SeekBar) findViewById(R.id.brightnessAmountGreen);
        brightLevelGreen = (TextView) findViewById(R.id.brightLevelGreen);
        //Initlize Red Led
        brightnessAmountRed = (SeekBar) findViewById(R.id.brightnessAmountRed);
        brightLevelRed = (TextView) findViewById(R.id.brightLevelRed);


        new ConnectBT().execute();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //commands to be sent to bluetooth
        ledOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnLed();      //method to turn on
            }
        });

        ledOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffLed();   //method to turn off
            }
        });

        bluetoothDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });


        //Control slider of blue LED
        brightnessAmountBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    brightLevelBlue.setText(String.valueOf(progress));
                    try {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
            }
        }
        );

        //Control slider of green LED
        brightnessAmountGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                 if (fromUser == true) {
                     brightLevelGreen.setText(String.valueOf(progress));
                     try {
                         btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                     } catch (IOException e) {
                     }
                 }
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekbar) {
             }

             @Override
             public void onStopTrackingTouch(SeekBar seekbar) {
             }
         }
        );

        //Control slider of red LED
        brightnessAmountRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
               if (fromUser == true) {
                   brightLevelRed.setText(String.valueOf(progress));
                   try {
                       btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                   } catch (IOException e) {
                   }
               }
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekbar) {
           }

           @Override
           public void onStopTrackingTouch(SeekBar seekbar) {
           }
       }
        );
    }

    private void Disconnect() {
        if (btSocket != null) { //If the btSocket is busy
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout}

    }
    private void turnOffLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("TF".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }


    private void turnOnLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("TO".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }




    private class ConnectBT extends AsyncTask<Void, Void, Void> { // UI thread
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    BTAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = BTAdapter.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

            @Override
            protected void onPostExecute (Void result){
                super.onPostExecute(result);
                if (!ConnectSuccess) {
                    msg("Connection Failed. Try again.");
                    finish();
                } else {
                    msg("Connected.");
                    isBtConnected = true;
                }
                progress.dismiss();
            }
        }
    }








