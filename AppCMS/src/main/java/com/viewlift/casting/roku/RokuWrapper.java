package com.viewlift.casting.roku;

import android.os.Handler;
import android.os.Message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import rx.functions.Action1;

public class RokuWrapper {
    private final static String LOCATION = "LOCATION:";
    final static String RUN_LOCATION = "LOCATION";
    private final static String APPLICATION_URL = "Application-URL";
    final static int DEVICE_FOUND_MESSAGE_ID = 1;
    final static int DISCOVERY_COMPLETED_MESSAGE_ID = 2;
    final static int APP_LAUNCH_MESSAGE_ID = 3;
    final static int SHOW_LAUNCH_MESSAGE_ID = 5;
    final static int FILM_LAUNCH_MESSAGE_ID = 6;
    private final static int APP_DISCOVER_MESSAGE_ID = 4;
    static final int APP_STOPPED_MESSAGE_ID = 7;
    final static int APP_LAUNCH_FAILED_MESSAGE_ID = 8;
    final static int DISCOVER_DEVICE = 0;
    final static int DISCOVER_APP = 1;
    final static int ACTION_LAUNCH = 0;
    final static int ACTION_STOP = 1;
    private static final String TAG = RokuWrapper.class.getSimpleName();
    private static RokuWrapperEventListener mRokuWrapperEventListener;

    private String appRunUrl;
    private List<RokuDevice> rokuDevices = new ArrayList<>();
    private List<RokuDevice> localRokuDevices = new ArrayList<>();
    public static RokuDevice selectedRokuDevice;
    private boolean isRokuConnected = false;
    private RokuLaunchThread rokuLaunchThread;
    private RokuLaunchThreadParams rokuLaunchThreadParams;
    private static RokuWrapper rokuWrapper = new RokuWrapper();
    String  contentId="",contentType="",userId="";

    private boolean isRokuDiscoveryTimerRunning;

    public List<RokuDevice> getRokuDevices() {
        return rokuDevices;
    }
    String ROKU_ACTION_LAUNCH = "roku_action_launch";
    String ROKU_ACTION_PLAY = "roku_action_play";

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DEVICE_FOUND_MESSAGE_ID:
                    RokuDiscoveryEventData ded = (RokuDiscoveryEventData) msg.obj;
                    ded.activity.notifyAppUrlFound(ded.rokuDevice);
                    break;
                case DISCOVERY_COMPLETED_MESSAGE_ID:
                    Set<RokuDevice> set = new TreeSet<>(new Comparator<RokuDevice>() {
                        @Override
                        public int compare(RokuDevice o1, RokuDevice o2) {
                            if (o1.getRokuDeviceName().equalsIgnoreCase(o2.getRokuDeviceName())) {
                                return 0;
                            }
                            return 1;
                        }
                    });
                    set.addAll(localRokuDevices);

                    rokuDevices.clear();
                    rokuDevices.addAll(set);
                    localRokuDevices.clear();
                    if(mRokuWrapperEventListener!=null)
                    mRokuWrapperEventListener.onRokuDiscovered(rokuDevices);
                    break;
                case APP_LAUNCH_MESSAGE_ID:
                    appRunUrl = (String) msg.obj;
                    isRokuConnected = true;
                    try {
                        String appId = getAppIdByParsingXMLResponse(getAllApps());
                        selectedRokuDevice.setRokuAppId(appId);
                    } catch (XmlPullParserException | IOException e) {
                        //Log.e("RokuWrapper:", "" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                    if(mRokuWrapperEventListener!=null)
                        mRokuWrapperEventListener.onRokuConnected(selectedRokuDevice);
                    break;
                case APP_LAUNCH_FAILED_MESSAGE_ID:
                    if(mRokuWrapperEventListener!=null)
                        mRokuWrapperEventListener.onRokuConnectedFailed(((String) msg.obj));
                    break;
                case SHOW_LAUNCH_MESSAGE_ID:
                    appRunUrl = (String) msg.obj;
                    isRokuConnected = true;
                    try {
                        selectedRokuDevice.setAppRunUrl(new URL(appRunUrl));
                        String appId = getAppIdByParsingXMLResponse(getAllApps());
                        selectedRokuDevice.setRokuAppId(appId);
                    } catch (XmlPullParserException | IOException e) {
                        //Log.e(" RokuWrapper:", "" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    if(mRokuWrapperEventListener!=null)
                    mRokuWrapperEventListener.onRokuConnected(selectedRokuDevice);
                    break;

                case FILM_LAUNCH_MESSAGE_ID:
                    appRunUrl = (String) msg.obj;
                    isRokuConnected = true;
                    try {
                        selectedRokuDevice.setAppRunUrl(new URL(appRunUrl));
                        String appId = getAppIdByParsingXMLResponse(getAllApps());
                        selectedRokuDevice.setRokuAppId(appId);
                    } catch (XmlPullParserException | IOException e) {
                        //Log.e(" RokuWrapper:", "" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                    if(mRokuWrapperEventListener!=null)
                    mRokuWrapperEventListener.onRokuConnected(selectedRokuDevice);
                    break;
                case APP_DISCOVER_MESSAGE_ID:
                    break;
                case APP_STOPPED_MESSAGE_ID:
                    notifyAppStopped();
                    break;
                default:
                    break;
            }
        }
    };
    private Timer discoveryTimer;

    public void clearRokuDeviceList() {
        rokuDevices.clear();
    }

    private class DiscoveryTimerTask extends TimerTask {
        @Override
        public void run() {
            sendDiscoveryRequest();
        }
    }

    public void startDiscoveryTimer() {
        if (isRokuDiscoveryTimerRunning) return;
        //Log.d(TAG, "Starting discovery timer");
        discoveryTimer = new Timer();
        discoveryTimer.scheduleAtFixedRate(new DiscoveryTimerTask(), 0, 10000);
        isRokuDiscoveryTimerRunning = true;
    }

    public void stopDiscoveryTimer() {
        if (!isRokuDiscoveryTimerRunning) return;
        //Log.d(TAG, "Stopping discovery timer");
        discoveryTimer.cancel();
        isRokuDiscoveryTimerRunning = false;
//        rokuDevices.clear();
        localRokuDevices.clear();
    }

    public static RokuWrapper getInstance() {
               return rokuWrapper;
    }

    public void setListener(RokuWrapperEventListener listener) {
        if (listener instanceof RokuWrapperEventListener)
            mRokuWrapperEventListener = listener;
        else
            throw new RuntimeException(listener.getClass() + " must implement RokuWrapperEventListener");
    }
    public void removeListener() {
        mRokuWrapperEventListener=null;
    }
    private void sendDiscoveryRequest() {
        //Log.d(TAG, "sending roku discovery request");
        RokuThreadParams discoveryRokuThreadParams = new RokuThreadParams();
        discoveryRokuThreadParams.setAction(RokuWrapper.DISCOVER_DEVICE);
        discoveryRokuThreadParams.setIntData(3);
        RokuDiscoveryThread rokuDiscoveryThread = new RokuDiscoveryThread(RokuWrapper.this);
        rokuDiscoveryThread.setParams(discoveryRokuThreadParams);
        rokuDiscoveryThread.start();
    }



    public void sendAppLaunchRequest() {
        fetchAppIdIfNotExists(ROKU_ACTION_LAUNCH);
    }


    public void sendFilmLaunchRequest(String contentId,
                                      String contentType,
                                      String userId) {
        this.contentId=contentId;
        this.contentType=contentType;
        this.userId=userId;
        fetchAppIdIfNotExists(ROKU_ACTION_PLAY);
    }

    private void fetchAppIdIfNotExists(String strRokuAction) {
        String appId = null;
        if (selectedRokuDevice != null)
            appId = selectedRokuDevice.getRokuAppId();
        if (appId == null) {
            String urlSearchRokuDevices = "http://" + selectedRokuDevice.getUrl().getAuthority() + "/query/apps";
            getAllRokuDevices(urlSearchRokuDevices, strRokuAction);
        }
        else
            launchMediaApp(strRokuAction);

    }


    public void sendStopRequest() {
        if (selectedRokuDevice == null) return;
        rokuLaunchThread = new RokuLaunchThread(RokuWrapper.this);
        rokuLaunchThreadParams.setAction(RokuWrapper.ACTION_STOP);
        rokuLaunchThreadParams.setUrl(selectedRokuDevice.getUrl());
        rokuLaunchThread.setParams(rokuLaunchThreadParams);
        rokuLaunchThread.start();
    }

    String getUPnPLocation(String M_SEARCH_Response) {
        int start = M_SEARCH_Response.indexOf(LOCATION);
        start += LOCATION.length() + 1;
        String location = M_SEARCH_Response.substring(start);
        int end = location.indexOf('\r', 0);
        location = location.substring(0, end);
        return location;
    }

    String sendDeviceDescriptionRequest(String upnpLocation) {
        String appUrl = "";
        try {
            URL obj = new URL(upnpLocation);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            appUrl = getHeader(con, APPLICATION_URL);
            in.close();

            //print result

        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, e.getLocalizedMessage());
        }
        return appUrl;
    }


    private String getAppIdByParsingXMLResponse(String is) throws XmlPullParserException, IOException {
        boolean foundTGC = false;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(is));
        // TODO: Replace the following with the App ID retrieved from AppCMS
        String appId = "";
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.TEXT) {
                //if (xpp.getText().equals(CastingUtils.ROKU_APP_NAME)) {
                if (xpp.getText().equals(appId)) {
                    System.out.println("" + "found App ID");
                    foundTGC = true;
                    break;
                }
            } else if (eventType == XmlPullParser.START_TAG) {
                appId = xpp.getAttributeValue(null, "id");
            }
            eventType = xpp.next();
        }
        System.out.println("App Id " + appId);
        return foundTGC ? appId : "";
    }

    public String getDeviceName(String upnpLocation) {
        String appName = "";
        try {
            URL obj = new URL(upnpLocation);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(con.getInputStream());
            NodeList nodeList = doc.getElementsByTagName("device");

            Node node = nodeList.item(0);

            Element element = (Element) node;

            appName = element.getElementsByTagName("friendlyName").item(0).getTextContent();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }

    String getHeader(HttpURLConnection con, String header) {
        int idx = (con.getHeaderFieldKey(0) == null) ? 1 : 0;
        while (true) {
            String key = con.getHeaderFieldKey(idx);
            if (key == null)
                break;
            if (header.equalsIgnoreCase(key))
                return con.getHeaderField(idx);
            ++idx;
        }
        return "";
    }

    private void notifyAppUrlFound(RokuDevice rokuDevice) {
        localRokuDevices.add(rokuDevice);
        //Log.d(TAG, rokuDevice.getRokuDeviceName() + " added to local");
    }

    private void notifyAppStopped() {
        if(mRokuWrapperEventListener!=null)
        mRokuWrapperEventListener.onRokuStopped();
        appRunUrl = null;
        selectedRokuDevice = null;
        isRokuConnected = false;
    }

    public RokuDevice getSelectedRokuDevice() {
        return selectedRokuDevice;
    }

    public boolean isRokuConnected() {
        return isRokuConnected;
    }

    public void setRokuConnected(boolean rokuConnected) {
        isRokuConnected = rokuConnected;
    }

    public void setSelectedRokuDevice(RokuDevice selectedRokuDevice) {
        RokuWrapper.selectedRokuDevice = selectedRokuDevice;
    }

    public boolean isRokuDiscoveryTimerRunning() {
        return isRokuDiscoveryTimerRunning;
    }

    public interface RokuWrapperEventListener {
        void onRokuDiscovered(List<RokuDevice> rokuDeviceList);

        void onRokuConnected(RokuDevice selectedRokuDevice);

        void onRokuStopped();

        void onRokuConnectedFailed(String obj);
    }



    public void getAllRokuDevices(String strUrl, final String strRokuAction) {

        GetRokuDevicesAsyncTask.Params params =
                new GetRokuDevicesAsyncTask.Params.Builder().url(strUrl).build();

        new GetRokuDevicesAsyncTask(
                new Action1<String>() {
                    @Override
                    public void call(String responseRokuDevices) {
                        try {
                            selectedRokuDevice.setRokuAppId(getAppIdByParsingXMLResponse(responseRokuDevices));
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        launchMediaApp(strRokuAction);

                    }

                }).execute(params);

    }



    private void launchMediaApp(String strRokuAction){
        if (strRokuAction.equalsIgnoreCase(ROKU_ACTION_LAUNCH)) {
            rokuLaunchThreadParams = new RokuLaunchThreadParams();
            rokuLaunchThread = new RokuLaunchThread(RokuWrapper.this);
            rokuLaunchThreadParams.setAction(RokuWrapper.ACTION_LAUNCH);
            rokuLaunchThreadParams.setPlayStart(0);
            rokuLaunchThreadParams.setContentType(RokuLaunchThreadParams.CONTENT_TYPE_APP);
            rokuLaunchThreadParams.setUrl(selectedRokuDevice.getUrl());
            rokuLaunchThreadParams.setRokuAppId(selectedRokuDevice.getRokuAppId());
            rokuLaunchThread.setParams(rokuLaunchThreadParams);
            rokuLaunchThread.start();
        } else if (strRokuAction.equalsIgnoreCase(ROKU_ACTION_PLAY)) {
            rokuLaunchThreadParams = new RokuLaunchThreadParams();
            rokuLaunchThread = new RokuLaunchThread(RokuWrapper.this);
            rokuLaunchThreadParams.setAction(RokuWrapper.ACTION_LAUNCH);
            rokuLaunchThreadParams.setPlayStart(0);
            rokuLaunchThreadParams.setUrl(selectedRokuDevice.getUrl());
            rokuLaunchThreadParams.setContentType(RokuLaunchThreadParams.CONTENT_TYPE_FILM);
            rokuLaunchThreadParams.setContentId(contentId);
            rokuLaunchThreadParams.setUserId(userId);
            rokuLaunchThreadParams.setRokuAppId(selectedRokuDevice.getRokuAppId());
            rokuLaunchThreadParams.setContentType(contentType);
            rokuLaunchThread.setParams(rokuLaunchThreadParams);
            rokuLaunchThread.start();
        }
    }
    private String getAllApps() {
        try {
            URL obj = new URL("http://" + selectedRokuDevice.getUrl().getAuthority() + "/query/apps");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            InputStream inputStream = con.getInputStream();


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            String res = response.toString();

            //Log.d("getAllApps response", res);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("getAllApps exception", e.toString());

            return "";
        }
    }

}
