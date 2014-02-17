package cc.pnq.spark.home;

import android.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import java.net.*;
import java.io.*;
import java.lang.Throwable;
import com.loopj.android.http.*;
import org.json.*;
import com.parse.*;

public class MainActivity extends Activity
{
    private Toast notifier;
    private ImageView img;
    private String server    = "http://192.168.0.104:3000";
    private String pinName   = "D5";
    private Boolean turnedOn = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initParse();
        refreshState();
    }

    private void initParse()
    {
        Parse.initialize(
            this,
            "4v6PitaPLi7g5Ytx7gY9gOBxDRpgXUgJlfmJeSf6",
            "jTl6X4jVZ7eNEtLQq4C7vNyenMOXq3kBVirGRrlr"
        );
        ParseAnalytics.trackAppOpened(getIntent());
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    private void refreshState() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get( server + "/pin_states/" + pinName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                String state = result.optString("state", "OFF");
                turnedOn     = state.equals( "ON" );
                msg( result.optBoolean("success") ? "状态更新完毕" : "状态更新失败" );
                updateBg();

                
            }

            @Override
            public void onFailure(Throwable e, JSONObject response) {
                msg( "连接失败 :(" );
            }
        });

    }

    private void updateBg() {
        img = (ImageView) findViewById( R.id.bgimg );
        if( img != null ) {
            img.setImageResource( turnedOn ? R.drawable.on : R.drawable.off );
        }
    }


    public void onTurnOn(View view) {
        if ( !turnedOn ) {
            turnedOn = true;
            updateBg();
            saveState();
        }
    }

    public void onTurnOff(View view) {
        if ( turnedOn ) {
            turnedOn = false;
            updateBg();
            saveState();
        }
    }

    private void saveState() {

        RequestParams params = new RequestParams();
        params.put("pin",   pinName );
        params.put("state", getStateText() );

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("pin", pinName);
        testObject.put("state", getStateText());
        testObject.saveInBackground();

        AsyncHttpClient client = new AsyncHttpClient();
        client.post( server + "/pin_states", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                msg( result.optBoolean("success") ? "保存成功" : "保存失败" );
            }

            @Override
            public void onFailure(Throwable e, JSONObject response) {
                msg( "保存失败 :(" );
            }
        });
    }     

    private String getStateText() {
        return turnedOn ? "ON" : "OFF";
    }

    private void msg( Object obj ) {
        if( notifier != null ) {
            notifier.cancel();
        }
        notifier = Toast.makeText(getApplicationContext(), String.valueOf(obj), 1000);
        notifier.show();
    }

    private void showError( Exception e ) {
        msg("Error: " + e.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onClickTasksMenu( item );
        return true;
    }

    public void onClickTasksMenu(MenuItem menu) {
        Intent intent     = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }

}
