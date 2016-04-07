package com.robotcontroller.universalrobotcontroller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothPair extends AppCompatActivity {

    private BluetoothAdapter BTAdapter = null;
    private Set<BluetoothDevice> pairedDevices;

    Button viewDevices;
    ListView deviceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_pair);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewDevices = (Button)findViewById(R.id.DevicesView);
        deviceList = (ListView)findViewById(R.id.listConnectedDevices);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        //Checks if device has a bluetooth module
        if(BTAdapter == null) {
           //if no module, tells user and then closes app
            new AlertDialog.Builder(this)
                    .setTitle("Not Compatible")
                    .setMessage("Your device does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        //if device has bluetooth, checks if it is enabled or not; if not ask to enable it
        } else if (!BTAdapter.isEnabled()) {
                Intent blueToothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(blueToothOn);
        }

        viewDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pariedDevicesList();
            }
        });
    }

    private void pariedDevicesList() {
        pairedDevices = BTAdapter.getBondedDevices();

        ArrayList btList = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                btList.add(bt.getName() + "\n" + bt.getAddress()); //Devices name and address
            }
        } else {
            Toast.makeText(getApplicationContext(), "There are no paried devices found", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, btList);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3) {
            //Retrive the MAC address of the device
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Start next activity
            //Intent i = new Intent(BluetoothPair.this, ledControl.class);

            //Switch to ledControl activity
          //  i.putExtra(EXTRA_ADDRESS, address);
          //  startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_pair, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
