package mapsted.com.myapplication.model;

import org.json.JSONException;
import org.json.JSONObject;


/*
    BuildingData  has an Array of Objects
    This class is defined to retrieve objects in this array.
 */
public class Building {
    private int id;
    private String name;
    private String city;
    private String state;
    private String country;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    //Returns true if a building is located in a given address

    public boolean containsAddress(String address) {
        return this.city.contains(address) ||
                this.state.contains(address) ||
                this.country.contains(address);
    }

    public Building(int id, String name, String city, String state, String country) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    //Returns an object of this class, is used in BuildingProvider class for loading the data
    public static Building parse(JSONObject jsonObject) throws JSONException {
        return new Building(
                jsonObject.getInt("building_id"),
                jsonObject.getString("building_name"),
                jsonObject.getString("city"),
                jsonObject.getString("state"),
                jsonObject.getString("country")
        );
    }
}
