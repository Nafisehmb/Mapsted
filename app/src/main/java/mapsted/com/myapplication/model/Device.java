package mapsted.com.myapplication.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/*
    AnalyticsData has an Array of Objects
    Device class is defined to retrieve objects in this array.
    There is an object which is an array of objects which can be retrieved by Purchase class.
    By using Hashtable for a variable we have stored array of objects in an object.
 */

public class Device {
    private String codeName;
    private String marketName;
    private String manufacturer;
    private String model;
    private Hashtable<Integer, ArrayList<Purchase>> purchaseHistory;

    public String getCodeName() {
        return codeName;
    }

    public String getMarketName() {
        return marketName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public Hashtable<Integer, ArrayList<Purchase>> getPurchaseHistory() {
        return purchaseHistory;
    }

    public Device(String codeName, String marketName, String manufacturer, String model,
                  Hashtable<Integer, ArrayList<Purchase>> purchaseHistory) {
        this.codeName = codeName;
        this.marketName = marketName;
        this.manufacturer = manufacturer;
        this.model = model;
        this.purchaseHistory = purchaseHistory;
    }
    //Returns an object of this class, is used in DeviceProvider class for loading the data
    public static Device parse(JSONObject jsonObject) throws JSONException {

        //Building the "usage_statistics" Object
        JSONArray historyJsonArray = jsonObject
                .getJSONObject("usage_statistics")
                .getJSONArray("session_infos"); //array of objects

        Hashtable<Integer, ArrayList<Purchase>> purchaseHistory = new Hashtable<>();

        //Building "session_info" that contains array of objects(which are array of objects)
        for (int index = 0; index < historyJsonArray.length(); index++) {
            JSONObject historyObject = historyJsonArray.getJSONObject(index);

            // for each one of the "session_info" objects, its id and purchase info retrieved here
            int buildingId = historyObject.getInt("building_id");
            JSONArray purchaseArray = historyObject.getJSONArray("purchases");

            // Checks if the building is already in Hashtable, it won't be added
            if (!purchaseHistory.contains(buildingId)) {
                purchaseHistory.put(buildingId, new ArrayList<Purchase>(purchaseArray.length()));
            }
            //For all the buildings in a session info all their purchase information is added b calling purchase class
            for (int i = 0; i < purchaseArray.length(); i++) {
                purchaseHistory.get(buildingId).add(Purchase.parse(purchaseArray.getJSONObject(i), buildingId));
            }
        }

        return new Device(
                jsonObject.getString("codename"),
                jsonObject.getString("market_name"),
                jsonObject.getString("manufacturer"),
                jsonObject.getString("model"),
                purchaseHistory
        );
    }
}
