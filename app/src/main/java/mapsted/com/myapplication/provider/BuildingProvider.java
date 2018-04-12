package mapsted.com.myapplication.provider;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import mapsted.com.myapplication.ThisApp;
import mapsted.com.myapplication.helper.C;
import mapsted.com.myapplication.model.Building;

/*
    BuildingProvider loads information from BuildingData url
 */
public class BuildingProvider implements IBuildingProvider<Building> {
    private String TAG = BuildingProvider.class.getSimpleName();
    private List<Building> items = new ArrayList<>();
    private Listener listener;

    //To have asynchronous callbacks we use Listener
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void loadAll() {
        JsonArrayRequest request = new JsonArrayRequest(C.URL_BUILDING, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    //Array of information contains Building type variables
                    ArrayList<Building> items = new ArrayList<>(response.length());

                    // Data are loaded by calling "parse" function from Building class
                    for (int index = 0; index < response.length(); index++) {
                        items.add(Building.parse(response.getJSONObject(index)));
                    }

                    //To have store data in "items" of BuildingProvider class( we are now in loadAll())
                    BuildingProvider.this.items = items;
                    Log.i(TAG, "From JSON: " + items);

                    if (BuildingProvider.this.listener != null) {
                        BuildingProvider.this.listener.onLoad();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    if (BuildingProvider.this.listener != null) {
                        BuildingProvider.this.listener.onError(e);
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildingProvider.this.listener != null) {
                    BuildingProvider.this.listener.onError(error);
                }
            }
        });
        //To load all data in AnalyticsData url the request is inserted in a queue by "ThisApp" class
        ThisApp.getRequestQueue().add(request);
    }
    // Return a Building variable (Building's information)for a given ID from BuildingData url
    public Building getById(int buildingId) {
        for(Building building : this.items) {
            if (building.getId() == buildingId) {
                return building;
            }
        }

        return null;
    }

    public interface Listener {
        void onLoad();
        void onError(Exception ex);
    }
}
