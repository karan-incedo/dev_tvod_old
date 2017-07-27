package com.viewlift.casting.roku;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;

public class RokuDiscoveryThread extends Thread {
    private RokuWrapper client;
    private RokuThreadParams params;
    private String TAG="RokuDiscoveryThread";

    public RokuDiscoveryThread(RokuWrapper client) {
        this.client = client;
    }

    public void setParams(RokuThreadParams params) {
        this.params = params;
    }

    public void discoverDevice() {
        try {
            int readCount = 0;
            String M_SEARCH = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\nMAN: \"ssdp:discover\"\r\nMX: seconds to delay response\r\nST: urn:dial-multiscreen-org:service:dial:1\r\nUSER-AGENT: RokuCastClient";
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(1000);
            InetAddress IPAddress = InetAddress.getByName("239.255.255.250");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            sendData = M_SEARCH.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 1900);
            clientSocket.send(sendPacket);
            String response = "";
            while (readCount < this.params.getIntData()) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    clientSocket.receive(receivePacket);
                    response = new String(receivePacket.getData());
                    String upnpLocation = this.client.getUPnPLocation(response);
                    //Move the following function to this thread class
                    String appUrl = this.client.sendDeviceDescriptionRequest(upnpLocation);
                    String deviceName = this.client.getDeviceName(upnpLocation);
                    RokuDevice rokuDevice = new RokuDevice(new URL(appUrl), deviceName, null);
                    Message msg = Message.obtain();
                    msg.what = RokuWrapper.DEVICE_FOUND_MESSAGE_ID;
                    msg.obj = new RokuDiscoveryEventData(this.client, rokuDevice);
                    System.out.println("********************** appUrl ********************" + appUrl);
                    this.client.handler.sendMessage(msg);
                } catch (SocketTimeoutException ste) {
                    Log.d(TAG, "SocketTimeoutException: "+ste.getLocalizedMessage());
                }
                readCount++;
            }
            clientSocket.close();
            //Tell the main UI that the Discover button can now be enabled again
            Message uiMsg = Message.obtain();
            uiMsg.what = RokuWrapper.DISCOVERY_COMPLETED_MESSAGE_ID;
            Log.d(TAG, "sending result back to Wrapper");
            this.client.handler.sendMessage(uiMsg);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void discoverApp() {
        try {
            String url = this.params.getUrl().toString();
            URL obj = new URL(url + "/TheGreatCoursesPlus");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            String res = response.toString();
            Log.d("App info response", res);

            /*Intent intent = new Intent(this.client, AppInfoActivity.class);
            intent.putExtra("appInfo", res);
            Message uiMsg = Message.obtain();
            uiMsg.what = RokuWrapper.APP_DISCOVER_MESSAGE_ID;
            uiMsg.obj = intent;
            this.client.handler.sendMessage(uiMsg);*/
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void run() {
        switch (this.params.getAction()) {
            case RokuWrapper.DISCOVER_DEVICE:
                this.discoverDevice();
                break;
            case RokuWrapper.DISCOVER_APP:
                this.discoverApp();
                break;
            default:
                break;
        }
    }

}
