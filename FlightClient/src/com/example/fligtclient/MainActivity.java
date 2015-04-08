package com.example.fligtclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{

	//gps����
	LocationManager location = null;
	TextView textView;
	Button FlightButton;

	//������� ����
	//String IP = "220.67.133.58";
	String IP = "220.67.133.58";
	int PORT = 8080;
	boolean status = false;

	Socket socket;
	DataOutputStream dos;
	DataInputStream Input;
	SetSocket setSocket;
	MessageReciver messageReceiver;
	
	
	//�ӽ� gps��
	double tempLat = 37.6033339;
	double tempLon = 126.86770150000007;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = (TextView)findViewById(R.id.TextView);
		FlightButton = (Button)findViewById(R.id.FlightButton);

		location = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Toast.makeText(this, "Starting....", Toast.LENGTH_SHORT).show();

		
		Criteria criteria = new Criteria();

		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		String provider = location.getBestProvider(criteria, true);

		location.requestLocationUpdates(provider, 1000, 0, this);

		if (setSocket == null) {
			setSocket = new SetSocket(IP, PORT);
			setSocket.start();
			messageReceiver = new MessageReciver();
		} else
			Toast.makeText(getApplicationContext(), "�̹� ���� ���Դϴ�.",
					Toast.LENGTH_SHORT).show();
			
		//�ӽ� �׽�Ʈ�� gps ��
		FlightButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if(status == false)
					{
						//dos.writeBytes("s'FlightClient is already connect....");//ó�� button�� �������� �����°�
						//dos.flush();
						status = true;
					}
					else
					{
						tempLon = tempLon + 0.0001;
						tempLat = tempLat + 0.0001;
						dos.writeBytes(tempLat + "," + tempLon +"\n"); // �׵ڷ� button�� ������ �̰�
						dos.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		textView.append( latitude + "," + longitude + "\n");
		
		try {
			dos.writeBytes(latitude + "," + longitude + "\n");
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public class SetSocket extends Thread {

		int PORT;
		String IP;

		public SetSocket(String IP, int PORT) {
			this.IP = IP;
			this.PORT = PORT;
		}

		public void run() {
			try {
				socket = new Socket(IP, PORT);
				dos = new DataOutputStream(socket.getOutputStream());
				Input = new DataInputStream(socket.getInputStream());
				
				messageReceiver.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	public String chatMessage;

	public class MessageReciver extends Thread {
		@SuppressWarnings("deprecation")
		public void run() {
			try {
				String received;
				
				while ((received = Input.readLine()) != null) {						
					
					chatMessage = received;
								
					Message message = handler.obtainMessage(1, received);
					handler.sendMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void print(Object message) {
		String[] receiveResult;
		char checkMsg;
		String Str = null;
		
		receiveResult = message.toString().split("'");
		
		checkMsg =receiveResult[0].charAt(0);
		Str = receiveResult[1];
		if(checkMsg == 's')
			textView.setText(Str);
		else
			textView.append(Str);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			super.handleMessage(message);
			print(chatMessage);
		}
	};
}
