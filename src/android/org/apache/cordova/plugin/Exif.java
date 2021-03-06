package org.apache.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.ExifInterface;

/**
 * This class echoes a string called from JavaScript.
 */
public class Exif extends CordovaPlugin {
    ExifInterface exif;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("setlocation")) {
            String filename = args.getString(0);
            String latitude = args.getString(1);
            String longitude = args.getString(2);
            this.exif_f(filename, latitude, longitude, callbackContext);
            return true;
        } else if (action.equals("wifiinfo")) {
            this.wifiinfo(callbackContext);
            return true;
        }
        return false;
    }

    private void wifiinfo(CallbackContext callbackContext) {
		Context context = cordova.getActivity().getApplicationContext();
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		JSONObject obj = new JSONObject();
		try {
			JSONObject activity = new JSONObject();
			activity.put("BSSID", wifiInfo.getBSSID());
			activity.put("HiddenSSID", wifiInfo.getHiddenSSID());
			activity.put("SSID", wifiInfo.getSSID());
			activity.put("MacAddress", wifiInfo.getMacAddress());
			activity.put("IpAddress", wifiInfo.getIpAddress());
			activity.put("NetworkId", wifiInfo.getNetworkId());
			activity.put("RSSI", wifiInfo.getRssi());
			activity.put("LinkSpeed", wifiInfo.getLinkSpeed());
			obj.put("activity", activity); 

			JSONArray available = new JSONArray();
	        for (ScanResult scanResult : wifiManager.getScanResults()) {
	        	JSONObject ap = new JSONObject();
	        	ap.put("BSSID", scanResult.BSSID);
	        	ap.put("SSID", scanResult.SSID);
	        	ap.put("frequency", scanResult.frequency);
	        	ap.put("level", scanResult.level);
	        	//netwrok.put("timestamp", String.valueOf(scanResult.timestamp));
	        	ap.put("capabilities", scanResult.capabilities);
	        	available.put(ap);
	        }
	        obj.put("available", available); 


		} catch (JSONException e) {
			e.printStackTrace();
			callbackContext.error("JSON Exception");
		}
		callbackContext.success(obj);
	}
	
    private void exif_f(String filename, String latitude, String longitude, CallbackContext callbackContext) {
        if (filename != null && filename.length() > 0 && latitude != null && latitude.length() > 0 && longitude != null && longitude.length() > 0) {
			try {
				exif = new ExifInterface(filename);
				int num1Lat = (int)Math.floor(latitude);
				int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
				double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

				int num1Lon = (int)Math.floor(longitude);
				int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
				double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");

				if (latitude > 0) {
					exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
				} else {
					exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
				}

				if (longitude > 0) {
					exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
				} else {
					exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
				}

				exif.saveAttributes();
				callbackContext.success(filename);
			} catch (IOException e) {
				callbackContext.error(e.getLocalizedMessage());
			} 
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
