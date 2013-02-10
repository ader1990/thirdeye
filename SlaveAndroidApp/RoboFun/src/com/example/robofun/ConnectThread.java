package com.example.robofun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectThread extends Thread {
	// private UUID MY_UUID = null;
	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	private BluetoothAdapter mBluetoothAdapter;

	public ConnectThread(BluetoothDevice device,
			BluetoothAdapter mBluetoothAdapter) {
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		this.mBluetoothAdapter = mBluetoothAdapter;
		mmDevice = device;

	}

	public void run() {
		// Cancel discovery because it will slow down the connection // Get a
		// BluetoothSocket to connect with the given BluetoothDevice

		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        try {
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			mmSocket.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			OutputStream mmOutputStream = mmSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			InputStream mmInputStream = mmSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void manageConnectedSocket(BluetoothSocket mmSocket2) {
		// TODO Auto-generated method stub

	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}