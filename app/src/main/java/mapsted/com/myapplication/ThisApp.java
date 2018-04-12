package mapsted.com.myapplication;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

 //extending the Application. use Volley Library to make network calls, creating requests and adding them to a queue
public class ThisApp extends Application {
    private static RequestQueue REQUEST_QUEUE;

    public static RequestQueue getRequestQueue() {
        return REQUEST_QUEUE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        REQUEST_QUEUE = Volley.newRequestQueue(this);
    }
}
