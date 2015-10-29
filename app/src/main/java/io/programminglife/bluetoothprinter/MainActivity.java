package io.programminglife.bluetoothprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.programminglife.bluetoothprinter.listeners.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity {

    private static final String tag = MainActivity.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PairedBtDevicesAdapter mPairedBtDevicesAdapter;
    private ImageView mPrinterIcon;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;

    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread mWorkerThread;

    private List<BluetoothDevice> mPairedDevices;
    private byte[] readBuffer;
    private int readBufferPosition;
    private int counter;
    private volatile boolean stopWorker;


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
        mPairedDevices = findPairedBtDevices();
        mPairedBtDevicesAdapter = new PairedBtDevicesAdapter(mPairedDevices);
        mRecyclerView.setAdapter(mPairedBtDevicesAdapter);

        mPrinterIcon = (ImageView)findViewById(R.id.printer_icon);
        mPrinterIcon.setVisibility(View.INVISIBLE);

        setInteraction();
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

    private void setInteraction() {
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            openBtConnection();
                            mPrinterIcon.setVisibility(View.VISIBLE);
                            mDevice = mPairedDevices.get(position);
                        } catch (IOException e) {
                            Log.e(tag, e.getMessage(), e);
                        }
                    }
                })
        );
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

    private void openBtConnection() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            mmOutputStream = mSocket.getOutputStream();
            mmInputStream = mSocket.getInputStream();

            beginListenForData();

            Snackbar.make(mCoordinatorLayout, "Bluetooth connection opened", Snackbar.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (Exception e) {
            Log.e(tag, e.getMessage(), e);
        }
    }

    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            mWorkerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte readByte = packetBytes[i];
                                    if (readByte == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                         encodedBytes, 0,
                                                         encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Snackbar.make(mCoordinatorLayout, "Data: " + data, Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = readByte;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            mWorkerThread.start();
        } catch (NullPointerException e) {
            Log.e(tag, e.getMessage(), e);
        } catch (Exception e) {
            Log.e(tag, e.getMessage(), e);
        }
    }
}
