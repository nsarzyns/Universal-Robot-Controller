package com.robotcontroller.universalrobotcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    SeekBar brightnessAmount;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter BTAdapter = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    TextView brightLevel;



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
        bluetoothDisconnect = (Button) findViewById(R.id.brightnessAmount);
        brightnessAmount = (SeekBar) findViewById(R.id.brightnessAmount);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        brightnessAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
          {
              @Override
              public void onProgressChanged(SeekBar seekbar, int progress,
                                            boolean fromUser) {
                  if (fromUser == true) {
                      brightLevel.setText(String.valueOf(progress));
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








