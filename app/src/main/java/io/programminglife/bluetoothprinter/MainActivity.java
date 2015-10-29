package io.programminglife.bluetoothprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PairedBtDevicesAdapter mPairedBtDevicesAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_layout);

        mRecyclerView = (RecyclerView)findViewById(R.id.paired_bt_devices);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPairedBtDevicesAdapter = new PairedBtDevicesAdapter(findPairedBtDevices());
        mRecyclerView.setAdapter(mPairedBtDevicesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<BluetoothDevice> findPairedBtDevices() {
        List<BluetoothDevice> pairedBtDevices = new ArrayList<BluetoothDevice>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Snackbar.make(mCoordinatorLayout, "No bluetooth adapter available", Snackbar.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if(bondedDevices.size() > 0) {
            for(BluetoothDevice bluetoothDevice : bondedDevices) {
                pairedBtDevices.add(bluetoothDevice);
            }
        }

        return pairedBtDevices;
    }
}
