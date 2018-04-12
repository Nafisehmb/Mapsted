package mapsted.com.myapplication.provider;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mapsted.com.myapplication.ThisApp;
import mapsted.com.myapplication.helper.C;
import mapsted.com.myapplication.model.Device;

/*
    DeviceProvider loads information from AnalyticsData url
 */

public class DeviceProvider {
    private String TAG = DeviceProvider.class.getSimpleName();
    private List<Device> items = new ArrayList<>();
    private Listener listener;

    //To have asynchronous callbacks we use Listener
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void loadAll() {
        JsonArrayRequest request = new JsonArrayRequest(C.URL_ANALYTIC, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    //Array of information contains Device type variables
                    ArrayList<Device> items = new ArrayList<>(response.length());

                    // Data are loaded by calling "parse" function from Device class
                    for (int index = 0; index < response.length(); index++) {
                        items.add(Device.parse(response.getJSONObject(index)));
                    }

                    //To have access to the "items" of DeviceProvider class( we are now in loadAll())
                    DeviceProvider.this.items = items;
                    Log.i(TAG, "From JSON: " + items);

                    if (DeviceProvider.this.listener != null) {
                        DeviceProvider.this.listener.onLoad();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    if (DeviceProvider.this.listener != null) {
                        DeviceProvider.this.listener.onError(e);
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (DeviceProvider.this.listener != null) {
                    DeviceProvider.this.listener.onError(error);
                }
            }
        });
        //To load all data in AnalyticsData url the request is inserted in a queue by "ThisApp" class
        ThisApp.getRequestQueue().add(request);
    }
    //Return all matching data in AnalyticsData url with a specific given information
    public Iterable<Device> getAll() {
        return items;
    }

    // Return all data for a requested manufacturer from AnalyticsData url
    public Iterable<Device> getByManufacture(String name) {
        List<Device> query = new LinkedList<>();

        for(Device device : this.items) {
            if (device.getManufacturer().equals(name)) {
                query.add(device);
            }
        }

        return query;
    }


    public interface Listener {
        void onLoad();

        void onError(Exception ex);
    }
}
