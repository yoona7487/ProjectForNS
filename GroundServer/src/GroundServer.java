import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class GroundServer implements Runnable {

	ServerSocket server;
	boolean connectStatus = false;
	public GroundServer() throws IOException
	{
		server = new ServerSocket(8080);
	}
	public void run()
	{
		try
		{
			Socket conn;
			InputStream input1 = null;
			InputStream input2 = null;
			OutputStream output1 = null;
			OutputStream output2 = null;

			while(true)
			{
				System.out.println("GroundServer is waiting.....");
				conn = server.accept();
				
				
				if(connectStatus == false)
				{
					input1 = conn.getInputStream();
					output1 = conn.getOutputStream();
					connectStatus = true;
					System.out.println("FlightAndroid or GroudMonitor�� ����Ǿ����ϴ�. ");
				}
				else
				{
					input2 = conn.getInputStream();
					output2 = conn.getOutputStream();
					new BackUpConnection(input1 , output1 , input2 , output2);
					connectStatus = false;
					System.out.println("FlightAndroid and GroudMonitor�� ����Ǿ����ϴ�. ");
				}			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public static void main(String[] args)
	{
		try
		{
			GroundServer gcs = new GroundServer();
			new Thread(gcs).start();
			
			System.out.println("������ ����Ǿ����ϴ�.");

		}
		catch(IOException e)
		{
			System.out.println("������ ���� �� �� ����.");
		}
	}

}
