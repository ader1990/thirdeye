package com.example.robofun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private TextView tv;
	private Button connectButton;
	private Button syncButton;
	private String serverEndpoint = "http://thirdeye1.azurewebsites.net/api/commands";
	private static final int REQUEST_ENABLE_BT = 1107;
	private String bluetoothPairedDeviceName = "RN42-665A";
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothDevice arduinoBluetoothDevice = null;
	private BluetoothSocket arduinoBluetoothSocket = null;
	private String bluetoothFoundText = "Bluetooth Device Found.";
	private String bluetoothNotFoundText = "Bluetooth Device Not Found.";
	private String bluetoothDisabledText = "Bluetooth Disabled";
	private String bluetoothEnabledText = "Bluetooth Enabled.";
	private volatile Boolean inSync = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.statusText);
		connectButton = (Button) findViewById(R.id.arduConnect);
		syncButton = (Button) findViewById(R.id.sync);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				tv.setText(this.bluetoothEnabledText);
			} else {
				tv.setText(this.bluetoothDisabledText);
			}
		}
	}

	public void onActivateBt(View v) {
		Thread btActivatorThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mBluetoothAdapter = BluetoothAdapter
										.getDefaultAdapter();
								if (mBluetoothAdapter != null) {
									tv.setText(bluetoothFoundText);
									if (!mBluetoothAdapter.isEnabled()) {
										Intent enableBtIntent = new Intent(
												BluetoothAdapter.ACTION_REQUEST_ENABLE);
										startActivityForResult(enableBtIntent,
												REQUEST_ENABLE_BT);
									} else {
										tv.setText(bluetoothEnabledText);
									}
								} else {
									tv.setText(bluetoothNotFoundText);
								}
							}
						});
					}
				} catch (Exception e) {
				}
			}
		};
		btActivatorThread.start();
	}

	private void methodSendMessage(String message) {
		try {
			if (arduinoBluetoothSocket != null) {
				final String mess = message;
				Thread sendToArduinoThread = new Thread() {
					@Override
					public void run() {
						try {
							synchronized (this) {
								if (arduinoBluetoothSocket != null) {
									OutputStream mmOutputStream = arduinoBluetoothSocket
											.getOutputStream();
									mmOutputStream
											.write(mess.getBytes("UTF-8"));
								}
								try {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
										}
									});
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						} catch (Exception e) {
						}
					}
				};
				sendToArduinoThread.start();
			}
		} catch (Exception e) {
		}
	}

	public void onSync(View v) {
		Thread internetSyncThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						if (inSync == false) {
							inSync = true;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									syncButton.setText("Stop sync");
								}
							});
							while (inSync) {
								// start run
								HttpClient httpclient = new DefaultHttpClient();
								HttpResponse response;
								try {
									response = httpclient.execute(new HttpGet(
											serverEndpoint));
									StatusLine statusLine = response
											.getStatusLine();
									if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
										ByteArrayOutputStream out = new ByteArrayOutputStream();
										response.getEntity().writeTo(out);
										out.close();
										String c = out.toString();
										String command=c.substring(c.indexOf("Value")+8,c.indexOf("Value")+9);
										final String responseString = command;
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												tv.setText(responseString);
											}
										});
										try {
											if (responseString.length() > 0) {
												if (arduinoBluetoothSocket != null) {
													OutputStream mmOutputStream = arduinoBluetoothSocket
															.getOutputStream();
													mmOutputStream
															.write(responseString
																	.substring(
																			0,
																			1)
																	.getBytes(
																			"UTF-8"));
													//mmOutputStream.close();
												}
											}
										} catch (Exception e) {
										}
									}
								} catch (Exception e) {
								}
								Thread.sleep(500);
							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									syncButton.setText("Sync");
								}
							});

						} else {
							inSync = false;
						}
					}
				} catch (Exception e) {
				}
			}
		};
		internetSyncThread.start();

	}

	public void onSendGoFront(View v) {
		methodSendMessage("F");
	}

	public void onSendGoBack(View v) {
		methodSendMessage("B");
	}

	public void onSendGoLeft(View v) {

		methodSendMessage("L");
	}

	public void onSendGoRight(View v) {

		methodSendMessage("R");
	}

	public void onConnectToArduino(View v) {
		Thread connectToArduinoThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						try {

							if (arduinoBluetoothSocket != null) {
								arduinoBluetoothSocket.close();
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										connectButton.setText("ArduConnect");
										tv.setText("Arduino Connected");
									}
								});
							} else {
								String btDeviceFoundTextCpy = "Arduino Not Found.";
								Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
										.getBondedDevices();
								if (pairedDevices.size() > 0) {
									for (BluetoothDevice device : pairedDevices) {
										String s = device.getName();
										if (device.getName().equals(
												bluetoothPairedDeviceName)) {
											btDeviceFoundTextCpy = "Arduino Found.";
											arduinoBluetoothDevice = device;
											break;
										}
									}
								}
								final String btDeviceFoundText = btDeviceFoundTextCpy;
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										tv.setText(btDeviceFoundText);
									}
								});
								if (arduinoBluetoothDevice != null) {
									try {
										mBluetoothAdapter.cancelDiscovery();
										Method m = arduinoBluetoothDevice
												.getClass()
												.getMethod(
														"createRfcommSocket",
														new Class[] { int.class });
										arduinoBluetoothSocket = (BluetoothSocket) m
												.invoke(arduinoBluetoothDevice,
														1);

										arduinoBluetoothSocket.connect();
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												tv.setText("Arduino Connected");
												connectButton
														.setText("ArduDisconnect");
											}
										});

									} catch (final Exception e) {
										arduinoBluetoothDevice = null;
										arduinoBluetoothSocket = null;
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												tv.setText(e.getMessage());
											}
										});
									}
								}
							}
						} catch (final Exception e) {
							arduinoBluetoothDevice = null;
							arduinoBluetoothSocket = null;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tv.setText(e.getMessage());
								}
							});
						}
					}

				} catch (Exception e) {
				}
			}
		};
		connectToArduinoThread.start();
	}

	public void onSubmit(View v) {
		final String name = "RN42-665A";
		BluetoothDevice deviceX = null;

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				String s = device.getName();
				if (device.getName().equals(name)) {
					deviceX = device;
					tv.setText("Device found");
					break;
				}
			}
		}
		final BluetoothDevice deviceX1 = deviceX;
		final String source = "F";
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						UUID uuid = UUID
								.fromString("00001101-0000-1000-8000-00805f9b34fb");
						BluetoothSocket mmSocket = null;
						try {
							ParcelUuid[] uuids = deviceX1.getUuids();

							tv.setText("createRfcommSocketToServiceRecord");
							mmSocket = deviceX1
									.createRfcommSocketToServiceRecord(uuids[0]
											.getUuid());
							tv.setText("Connecting");
							mmSocket.connect();
							tv.setText("Create outputStream");
							final OutputStream mmOutputStream = mmSocket
									.getOutputStream();
							mmOutputStream.write(source.getBytes("UTF-8"));
							final String link = "http://thirdeye.azurewebsites.net/api/commands";
							while (true) {
								Thread thread1 = new Thread() {
									@Override
									public void run() {
										try {
											synchronized (this) {
												wait(500);
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														// start run
														HttpClient httpclient = new DefaultHttpClient();
														HttpResponse response;
														String responseString = null;
														try {
															response = httpclient
																	.execute(new HttpGet(
																			link));
															StatusLine statusLine = response
																	.getStatusLine();
															if (statusLine
																	.getStatusCode() == HttpStatus.SC_OK) {
																ByteArrayOutputStream out = new ByteArrayOutputStream();
																response.getEntity()
																		.writeTo(
																				out);
																out.close();
																responseString = out
																		.toString();
																tv.setText("Write outputSrteam");
																try {
																	if (responseString
																			.length() > 0)
																		mmOutputStream
																				.write(responseString
																						.substring(
																								0,
																								1)
																						.getBytes(
																								"UTF-8"));
																} catch (UnsupportedEncodingException e) {
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	tv.setText("Error haha"
																			+ e.getMessage());
																	e.printStackTrace();
																} catch (IOException e) {
																	tv.setText("Error haha"
																			+ e.getMessage());
																	// TODO
																	// Auto-generated
																	// catch
																	// block
																	e.printStackTrace();
																}
																tv.setText(Math
																		.random()
																		+ "");
																// end run }
																// else {
																// Closes the
																// connection.
																response.getEntity()
																		.getContent()
																		.close();
																throw new IOException(
																		statusLine
																				.getReasonPhrase());
															}
														} catch (ClientProtocolException e) {
															// TODO Handle
															// problems..
															tv.setText("Error haha"
																	+ e.getMessage());
														} catch (IOException e) {
															// TODO Handle
															// problems..
															tv.setText("Error haha"
																	+ e.getMessage());
														}
													}
												});

											}
										} catch (InterruptedException e) {
											e.printStackTrace();
											tv.setText("Error haha"
													+ e.getMessage());
										}
									}
								};
								thread1.start();
							}

						} catch (Exception e) {
							// TODO: handle exception
							tv.setText("Error haha" + e.getMessage());
						}
					}
					;
				} catch (Exception e) {
					tv.setText("Error haha" + e.getMessage());
				}
			}
		};

		thread.start();
		// tv.setText("Connected");
		/*
		 * 
		 * Thread thread = new Thread() {
		 * 
		 * @Override public void run() { try { synchronized (this) { UUID uuid =
		 * UUID .fromString("00001101-0000-1000-8000-00805f9b34fb");
		 * BluetoothSocket mmSocket = null; try { ParcelUuid[] uuids =
		 * deviceX1.getUuids();
		 * 
		 * tv.setText("createRfcommSocketToServiceRecord"); mmSocket = deviceX1
		 * .createRfcommSocketToServiceRecord(uuids[0] .getUuid());
		 * tv.setText("Connecting"); mmSocket.connect();
		 * tv.setText("Create outputSrteam"); final OutputStream mmOutputStream
		 * = mmSocket .getOutputStream();
		 * 
		 * final String link = ("http://www.vla.ro/site/rebanc.php?id=15");
		 * while (true) { Thread thread = new Thread() {
		 * 
		 * @Override public void run() { try { synchronized (this) { wait(500);
		 * HttpClient httpclient = new DefaultHttpClient(); HttpResponse
		 * response; String responseString = null; try { response = httpclient
		 * .execute(new HttpGet( link)); StatusLine statusLine = response
		 * .getStatusLine(); if (statusLine .getStatusCode() ==
		 * HttpStatus.SC_OK) { ByteArrayOutputStream out = new
		 * ByteArrayOutputStream(); response.getEntity() .writeTo(out);
		 * out.close(); responseString = out .toString();
		 * 
		 * tv.setText("Write outputSrteam"); mmOutputStream .write(source
		 * .getBytes("UTF-8")); Thread.sleep(500); } else { // Closes the //
		 * connection. response.getEntity() .getContent() .close(); throw new
		 * IOException( statusLine .getReasonPhrase()); } } catch
		 * (ClientProtocolException e) { // TODO Handle // problems.. } catch
		 * (IOException e) { // TODO Handle // problems.. } runOnUiThread(new
		 * Runnable() {
		 * 
		 * @Override public void run() { tv.setText(Math .random() + ""); } });
		 * 
		 * } } catch (InterruptedException e) { e.printStackTrace(); } } };
		 * 
		 * } } catch (IOException e) { tv.setText("Failed " + e.getMessage());
		 * e.printStackTrace(); } } } catch (Exception e) { // TODO: handle
		 * exception } } }; thread.start(); // tv.setText("Connected");
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

}
