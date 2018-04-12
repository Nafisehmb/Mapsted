package mapsted.com.myapplication.model;

import org.json.JSONException;
import org.json.JSONObject;

import mapsted.com.myapplication.provider.IBuildingProvider;

/*
    Nested within the root object:
    AnalyticsData has an Array of Objects
    there is an Object,"session_infos", which is Array of Objects(which it also contains array of objects) it-self
    Purchase class is defined to gain access to the lowest(last) level of objects in this array of objects.
    */

public class Purchase {
    private int buildingId;
    private int itemId;
    private int categoryId;
    private double cost;

    private Building building;

    public int getBuildingId() {
        return buildingId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getCost() {
        return cost;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(IBuildingProvider<Building> buildingProvider) {
        this.building = buildingProvider.getById(this.buildingId);
    }



    public Purchase(int buildingId, int itemId, int categoryId, double cost) {
        this.buildingId = buildingId;
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.cost = cost;
    }


    //Returns an object of this class, is used in Device class (part of its objects)
    public static Purchase parse(JSONObject jsonObject, int buildingId) throws JSONException{
        return new Purchase(
                buildingId,
                jsonObject.getInt("item_id"),
                jsonObject.getInt("item_category_id"),
                jsonObject.getDouble("cost")
        );
    }

}
