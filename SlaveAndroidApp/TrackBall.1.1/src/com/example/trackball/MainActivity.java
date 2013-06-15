package com.example.trackball;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private TextView tv;
	private ImageButton connectButton;
	private ImageButton syncButton;
	private ImageButton bluetoothButton;
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
	private ImageButton trackBall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.Console);
		connectButton = (ImageButton) findViewById(R.id.arduConnect);
		syncButton = (ImageButton) findViewById(R.id.sync);
		bluetoothButton = (ImageButton) findViewById(R.id.btConnect);
		trackBall = (ImageButton) findViewById(R.id.TrackBall);
		trackBall.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, final MotionEvent event) {
				// TODO Auto-generated method stub

				final float centerX = ((View) v.getParent()).getWidth() / 2;
				final float centerY = ((View) v.getParent()).getHeight() / 2;
				TankMoveStrategy ms = new TankMoveStrategy(centerX, centerY);
				ArrayList<Move> moves = ms.getMoves(event.getRawX(),
						event.getRawY());
				String text = "Speed -> ";
				methodSendMessage(moves);
				for (int i = 0; i < moves.size(); i++) {
					text += moves.get(i).toString() + " ";
				}
				final String text1 = text;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv.setText(text1);
						if (android.os.Build.VERSION.SDK_INT > 10) {
							trackBall.setX(event.getRawX()
									- trackBall.getWidth() / 2);
							trackBall.setY(event.getRawY()
									- trackBall.getHeight() / 2);
						}
					}
				});
				return false;
			}
		});
	}

	@Override
	protected synchronized void onActivityResult(int requestCode,
			int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				bluetoothButton.setImageDrawable(getResources().getDrawable(
						R.drawable.bluetooth_active));
			} else {
				bluetoothButton.setImageDrawable(getResources().getDrawable(
						R.drawable.bluetooth_error));
				tv.setText(this.bluetoothDisabledText);
			}
		}
	}

	public synchronized void onActivateBt(View v) {
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
										bluetoothButton
												.setImageDrawable(getResources()
														.getDrawable(
																R.drawable.bluetooth_loading));
										Intent enableBtIntent = new Intent(
												BluetoothAdapter.ACTION_REQUEST_ENABLE);
										startActivityForResult(enableBtIntent,
												REQUEST_ENABLE_BT);
									} else {
										bluetoothButton
												.setImageDrawable(getResources()
														.getDrawable(
																R.drawable.bluetooth_active));
										tv.setText(bluetoothEnabledText);
									}
								} else {
									bluetoothButton
											.setImageDrawable(getResources()
													.getDrawable(
															R.drawable.bluetooth_error));
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

	private synchronized void methodSendMessage(ArrayList<Move> moves) {

		boolean tankStrategy = false;

		String message = "A";
		Move first = moves.get(0);
		Move second = moves.get(1);
		String firstCommand = first.getName().substring(0, 1);
		String secondCommand = second.getName().substring(0, 1);

		int firstValue = first.getValue();
		int secondValue = second.getValue();

		int finalVal1 = firstValue;
		int finalVal2 = secondValue;
		if (tankStrategy) {
			if (firstValue == secondValue && firstValue == 0) {

			} else {
				if (secondValue == 0) {
					if (firstCommand.equals("F"))
						secondCommand = "L";
					else
						secondCommand = "R";
					finalVal2 = finalVal1;
				}
				if (firstValue == 0) {
					if (secondCommand.equals("L")) {
						secondCommand = "R";
					} else {
						secondCommand = "L";
					}
					if (secondCommand.equals("R"))
						firstCommand = "F";
					else
						firstCommand = "B";
					finalVal1 = finalVal2;
				}
				if (firstValue > 0 && secondValue > 0) {
					if (firstCommand.equals("F")) {
						if (secondCommand.equals("R")) {
							secondCommand = "L";
							if (secondValue % 2 != 0)
								return;
							if (firstValue > secondValue) {
							} else {
								firstCommand = "B";
							}
							finalVal1 = firstValue
									- ((int) Math.floor(secondValue / 2));
							finalVal2 = firstValue;
						} else {
							if (secondValue % 2 != 0)
								return;
							if (firstValue > secondValue) {
							} else {
								secondCommand = "R";
							}
							finalVal2 = firstValue
									- ((int) Math.floor(secondValue / 2));
							finalVal1 = firstValue;
						}
					} else {
						if (secondCommand.equals("R")) {
							if (secondValue % 2 != 0)
								return;
							if (firstValue > secondValue) {
							} else {
								firstCommand = "F";
							}
							finalVal1 = firstValue
									- ((int) Math.floor(secondValue / 2));
							finalVal2 = firstValue;
						} else {
							secondCommand = "R";
							if (secondValue % 2 != 0)
								return;
							if (firstValue > secondValue) {
							} else {
								secondCommand = "L";
							}
							finalVal2 = firstValue
									- ((int) Math.floor(secondValue / 2));
							finalVal1 = firstValue;
						}
					}
				}
			}
		}
		message += firstCommand + Math.abs(finalVal1) + secondCommand
				+ Math.abs(finalVal2) + "Z";

		final String messageCpy = message.replace(" ", "");
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
									mmOutputStream.write(messageCpy
											.getBytes("ASCII"));
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

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	public synchronized void onSync(View v) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				tv.setText("Trying sync");
			}
		});
		if (haveNetworkConnection()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					tv.setText("Syncing");
				}
			});
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
										syncButton
												.setImageDrawable(getResources()
														.getDrawable(
																R.drawable.sync_active));
										tv.setText("Sync enabled");
									}
								});
								HttpClient httpclient = new DefaultHttpClient();
								HttpResponse response;
								HttpGet req=new HttpGet(
										serverEndpoint);
								req.addHeader("Content-Type","application/json");
								int nr=0;
								while (inSync) {
									// start run

									final int nr1=nr++;
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											tv.setText("WAiting "+ nr1);
										}
									});
									try {
										response = httpclient
												.execute(req);
										StatusLine statusLine = response
												.getStatusLine();
										if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
											ByteArrayOutputStream out = new ByteArrayOutputStream();
											response.getEntity().writeTo(out);
											out.close();
											String c = out.toString();
											JSONObject json = new JSONObject(c);
											String command=json.getString("Value");
											String commandFinal = "A";
											if (command.equals("B")) {
												commandFinal += "B1L0";
											}
											if (command.equals("R")) {

												commandFinal += "F1R4";
											}
											if (command.equals("L")) {

												commandFinal += "F1L4";
											}
											if (command.equals("F")) {

												commandFinal += "F1L0";
											}
											commandFinal += "Z";
											if (commandFinal.equals("AZ")) {
												commandFinal = "AF0L0Z";
											}
											final String responseString = commandFinal;
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
																		.getBytes("UTF-8"));
														// mmOutputStream.close();
													}
												}
											} catch (final Exception e) {
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														tv.setText(e
																.getMessage());
													}
												});
											}
										}
									} catch (final Exception e) {

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												tv.setText(e.getMessage());
											}
										});
									}
									Thread.sleep(200);
								}

							} else {
								inSync = false;
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										syncButton
												.setImageDrawable(getResources()
														.getDrawable(
																R.drawable.sync_faded));

										tv.setText("Sync disabled");

									}
								});
							}
						}
					} catch (Exception e) {
					}
				}
			};
			internetSyncThread.start();
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					syncButton.setImageDrawable(getResources().getDrawable(
							R.drawable.sync_error));

					tv.setText("Internet not enabled");
				}
			});
		}
	}

	public synchronized void onConnectToArduino(View v) {
		Thread connectToArduinoThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						try {

							if (arduinoBluetoothSocket != null) {
								arduinoBluetoothSocket.close();
								arduinoBluetoothSocket = null;
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// connectButton.setText("ArduConnect");
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												connectButton
														.setImageDrawable(getResources()
																.getDrawable(
																		R.drawable.arduino_faded));

											}
										});
										tv.setText("Arduino Disconnected");
									}
								});
							} else {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										connectButton
												.setImageDrawable(getResources()
														.getDrawable(
																R.drawable.arduino_loading));
									}
								});
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
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														connectButton
																.setImageDrawable(getResources()
																		.getDrawable(
																				R.drawable.arduino_active));
													}
												});
											}
										});

									} catch (final Exception e) {
										arduinoBluetoothDevice = null;
										arduinoBluetoothSocket = null;
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												connectButton
														.setImageDrawable(getResources()
																.getDrawable(
																		R.drawable.arduino_error));
											}
										});
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
									connectButton
											.setImageDrawable(getResources()
													.getDrawable(
															R.drawable.arduino_error));
								}
							});
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

	@Override
	public void onConfigurationChanged(Configuration c) {

	}

	@Override
	protected void onDestroy() {
		if (arduinoBluetoothSocket != null) {
			try {
				arduinoBluetoothSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			arduinoBluetoothSocket = null;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tv.setText("Arduino Disconnected");
				}
			});
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
