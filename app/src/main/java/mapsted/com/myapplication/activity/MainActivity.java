package mapsted.com.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

import mapsted.com.myapplication.R;
import mapsted.com.myapplication.model.Building;
import mapsted.com.myapplication.model.Device;
import mapsted.com.myapplication.model.Purchase;
import mapsted.com.myapplication.provider.BuildingProvider;
import mapsted.com.myapplication.provider.DeviceProvider;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private BuildingProvider buildingProvider;
    private DeviceProvider deviceProvider;

    private TextView txtSamsungTotalCost;
    private TextView txtTotalNumberOfItem47;
    private TextView txtTotalCostOfCategory7;
    private TextView txtTotalCostOfOntario;
    private TextView txtTotalCostOfUSA;
    private TextView txtMaxCostBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Text views to show the result for the questions
        txtSamsungTotalCost = findViewById(R.id.id_txt_samsung_total_cost);
        txtTotalNumberOfItem47 = findViewById(R.id.id_txt_total_number_item_47);
        txtTotalCostOfCategory7 = findViewById(R.id.id_txt_total_cost_category_7);
        txtTotalCostOfOntario = findViewById(R.id.id_txt_total_cost_province_ontario);
        txtTotalCostOfUSA = findViewById(R.id.id_txt_total_cost_country_usa);
        txtMaxCostBuilding = findViewById(R.id.id_txt_max_cost_building);

        //To have asynchronous callbacks
        buildingProvider = new BuildingProvider();
        buildingProvider.setListener(buildingListener);
        deviceProvider = new DeviceProvider();
        deviceProvider.setListener(deviceListener);

        //Load all the information in both urls
        buildingProvider.loadAll();
        deviceProvider.loadAll();
    }
    //Q.1.
    private double calculateTotalCostForManufacturer(String manufacturer) {
        double aggregate = 0;

        //Go over all the data in AnalyticsData, finds all with requested manufacturer
        Iterable<Device> samsungDevices = deviceProvider.getByManufacture(manufacturer);

        //For this manufacturer, go through all its purchases objects and add the costs together
        for (Device device : samsungDevices) {
            for (Integer key : device.getPurchaseHistory().keySet()) {
                for (Purchase purchase : device.getPurchaseHistory().get(key)) {
                    aggregate += purchase.getCost();
                }
            }
        }

        return aggregate;
    }

    //Q.2.
    private int calculateTotalNumberOfItemID(int itemId) {
        int aggregate = 0;

        //Go over all the data in AnalyticsData, finds all with requested itemId
        for (Device device : deviceProvider.getAll()) {

            /*  Go over all loaded data and checks the itemId,
                the key in purchaseHistory is buildingId
                check itemIds in each buildingId with requested one, if they are equal
                the counter is added by one*/
            for (Integer key : device.getPurchaseHistory().keySet()) {
                for (Purchase purchase : device.getPurchaseHistory().get(key)) {
                    if (purchase.getItemId() == itemId) {
                        aggregate++;
                    }
                }
            }
        }

        return aggregate;
    }

    //Q.3.
    private int calculateTotalCostOfCategoryID(int categoryId) {
        int aggregate = 0;

        //Go over all the data in AnalyticsData, finds all with requested categoryId
        for (Device device : deviceProvider.getAll()) {

              /*  Go over all loaded data and checks the categoryId,
                the key in purchaseHistory is buildingId
                check itemIds in each buildingId with requested one, if they are equal
                add the costs together*/
            for (Integer key : device.getPurchaseHistory().keySet()) {
                for (Purchase purchase : device.getPurchaseHistory().get(key)) {
                    if (purchase.getCategoryId() == categoryId) {
                        aggregate += purchase.getCost();
                    }
                }
            }
        }

        return aggregate;
    }

    //Q.4. and Q.5.
    private int calculateTotalCostOfAddress(String address) {
        int aggregate = 0;

        //Go over all the data in AnalyticsData, finds all costs in a requested address
        for (Device device : deviceProvider.getAll()) {

            /*  Go over all loaded data and checks the location,
                check the location of building with the requested one, if they are equal
                add the costs together*/
            for (Integer key : device.getPurchaseHistory().keySet()) {
                for (Purchase purchase : device.getPurchaseHistory().get(key)) {

                    //if the building is not recognized, it will be loaded from building's list
                    if(purchase.getBuilding() == null) {
                        purchase.setBuilding(this.buildingProvider);
                    }

                    if(purchase.getBuilding().containsAddress(address)) {
                        aggregate += purchase.getCost();
                    }
                }
            }
        }

        return aggregate;
    }

    //Q.6.
    private Building getBuildingWithMaxCost() {
        //costs for all buildings are stored in a Hashtable to find the maximum at the end
        Hashtable<Integer, Double> totalCost = new Hashtable<>();

        //Go over all the data in AnalyticsData, finds the max cost
        for (Device device : deviceProvider.getAll()) {

            //for each building, all the costs are added, and stored in a Hashtable

            for (Integer buildingId : device.getPurchaseHistory().keySet()) {
                double buildingTotalCost = 0;
                for (Purchase purchase : device.getPurchaseHistory().get(buildingId)) {
                    buildingTotalCost += purchase.getCost();
                }
                double previousTotalCost = totalCost.get(buildingId) == null ? 0 : totalCost.get(buildingId);
                totalCost.put(buildingId, previousTotalCost + buildingTotalCost);
            }
        }
        // Find the maximum cost in the Hashtable and return the Building (all information)
        double max = 0;
        int maxID = 0;
        for(Integer key : totalCost.keySet()) {
            if(totalCost.get(key) > max) {
                max = totalCost.get(key);
                maxID = key;
            }
        }

        return buildingProvider.getById(maxID);
    }


    private BuildingProvider.Listener buildingListener = new BuildingProvider.Listener() {
        @Override
        public void onLoad() {

        }

        @Override
        public void onError(Exception ex) {
            Toast.makeText(MainActivity.this,
                    "An error occurred!",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.getMessage());
        }
    };


    // Functions are called with a requested information to look up.
    private DeviceProvider.Listener deviceListener = new DeviceProvider.Listener() {
        @Override
        public void onLoad() {
            txtSamsungTotalCost.setText(String.valueOf(calculateTotalCostForManufacturer("Samsung")));
            txtTotalNumberOfItem47.setText(String.valueOf(calculateTotalNumberOfItemID(47)));
            txtTotalCostOfCategory7.setText(String.valueOf(calculateTotalCostOfCategoryID(7)));
            txtTotalCostOfOntario.setText(String.valueOf(calculateTotalCostOfAddress("Ontario")));
            txtTotalCostOfUSA.setText(String.valueOf(calculateTotalCostOfAddress("United States")));
            txtMaxCostBuilding.setText(String.valueOf(getBuildingWithMaxCost().getName()));
        }

        @Override
        public void onError(Exception ex) {
            Toast.makeText(MainActivity.this,
                    "An error occurred!",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, ex.getMessage());
        }
    };
}
