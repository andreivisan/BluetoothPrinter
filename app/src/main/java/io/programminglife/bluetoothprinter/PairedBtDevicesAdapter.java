package io.programminglife.bluetoothprinter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andrei on 10/29/15.
 */
public class PairedBtDevicesAdapter extends RecyclerView.Adapter<PairedBtDevicesAdapter.PairedBtDevicesViewHolder> {

    List<BluetoothDevice> pairedBtDevices;

    public PairedBtDevicesAdapter(List<BluetoothDevice> pairedBtDevicesDataSet) {
        this.pairedBtDevices = pairedBtDevicesDataSet;
    }

    public static class PairedBtDevicesViewHolder extends RecyclerView.ViewHolder {
        public TextView mPrinterName;

        public PairedBtDevicesViewHolder(View itemView) {
            super(itemView);

            mPrinterName = (TextView)itemView.findViewById(R.id.printer_name);
        }
    }

    @Override
    public PairedBtDevicesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bt_device_item, viewGroup, false);
        PairedBtDevicesViewHolder viewHolder = new PairedBtDevicesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PairedBtDevicesViewHolder pairedBtDevicesViewHolder, int position) {
        pairedBtDevicesViewHolder.mPrinterName.setText(pairedBtDevices.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return pairedBtDevices.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
