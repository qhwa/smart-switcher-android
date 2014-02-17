package cc.pnq.spark.home.service;

import cc.pnq.spark.home.CronTask;
import com.loopj.android.http.*;
import org.json.*;


public class CronTaskService {

    private static final String server = "http://192.168.0.104:3000";
    private static final AsyncHttpClient client = new AsyncHttpClient();


    static public void fetchTasks( JsonHttpResponseHandler handler )
    {
        client.get( server + "/cron_tasks.json", handler );
    }

    static public void update( CronTask task )
    {
        update( task, new JsonHttpResponseHandler() );
    }

    static public void update( CronTask task, JsonHttpResponseHandler handler )
    {
        RequestParams params = new RequestParams();

        params.put( "hour",     String.valueOf(task.hour) );
        params.put( "minute",   String.valueOf(task.minute) );
        params.put( "enabled",  String.valueOf(task.enabled) );
        /* 
         * why "turn_action" ?
         * 本来应该是 action, 但是 android-async-http 貌似会
         * 吞掉这个字段，无奈只好改名字
         */
        params.put( "turn_action", task.action );

        if( task.persisted ) {
            client.put( server + "/cron_tasks/" + task.id + ".json", params, handler );
        } else {
            client.post( server + "/cron_tasks.json", params, handler );
        }
    }

}
