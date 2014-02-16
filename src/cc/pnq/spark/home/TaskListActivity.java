package cc.pnq.spark.home;

import android.app.*;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;

import java.net.*;
import java.io.*;
import java.util.*;

import com.loopj.android.http.*;
import org.json.*;

public class TaskListActivity extends FragmentActivity
{
    private ListView lv;
    private ArrayList<CronTask> data = new ArrayList<CronTask>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.tasks );
        initList();
    }

    private void initList()
    {
        SimpleAdapter adapter = new SimpleAdapter(
            this,
            getData(),
            R.layout.cron_task,
            getFieldDef(),
            getViewDef()
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView             = super.getView( position, convertView, parent );
                final Map<String, Object> data = (Map<String, Object>) getItem( position );
                final int index          = position;

                final TextView tf = (TextView) rowView.findViewById( R.id.ct_time );
                tf.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        TimePickerFragment fragment = new TimePickerFragment() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                String text = hour + ":" + minute;
                                data.put( "time", text);
                                data.put( "hour", hour );
                                data.put( "minute", minute );
                                tf.setText( text );
                            }
                        };
                        int hour   = (Integer) data.get("hour");
                        int minute = (Integer) data.get("minute");
                        fragment.setDefaultTime( hour, minute );
                        fragment.show( getSupportFragmentManager(), "timePicker" );
                    }
                });

                ImageView img            = (ImageView) rowView.findViewById( R.id.ct_action );
                String action            = (String) data.get( "action" );
                img.setImageResource( action == "turnOn" ? R.drawable.switch_on : R.drawable.switch_off );

                img.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        ImageView img = (ImageView) v;
                        if( data.get( "action" ) == "turnOn" ) {
                            data.put( "action", "turnOff" );
                            img.setImageResource( R.drawable.switch_off );
                        } else {
                            data.put( "action", "turnOn" );
                            img.setImageResource( R.drawable.switch_on );
                        }
                    }
                });

                CompoundButton tgBtn = (CompoundButton) rowView.findViewById( R.id.ct_enabled );
                Boolean enabled      = (Boolean) data.get( "enabled" );
                tgBtn.setChecked( enabled );

                tgBtn.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CompoundButton btn = (CompoundButton) v;
                        if( btn.isChecked() ) {
                            msg( "[" + index + "] " + data.toString() + " enabled" );
                        } else {
                            msg( "[" + index + "] " + data.toString() + " disabled" );
                        }
                    }
                });

                return rowView;
            }
        };

        lv = (ListView) findViewById( R.id.listview );
        lv.setAdapter( adapter );
    }

    private List<Map<String, Object>> getData()
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        data.clear();

        for( int i=0; i< 9 ; i++ ) 
        {
            CronTask task = new CronTask();
            task.hour = 9;
            task.minute = 10;
            task.weekdayOnly = true;
            data.add( task );

            Map<String, Object> map = new HashMap<String, Object>();
            map.put( "time",        task.getTime() );
            map.put( "hour",        task.hour );
            map.put( "minute",      task.minute );
            map.put( "action",      task.action );
            map.put( "weekdayOnly", task.weekdayOnly );
            map.put( "enabled",     task.enabled );
            list.add( map );
        }

        return list;
    }

    private String[] getFieldDef()
    {
        String[] fields = { "time", "action" };
        return fields;
    }

    private int[] getViewDef()
    {
        int[] views = { R.id.ct_time, R.id.ct_action };
        return views;
    }

    private void msg( String text ) {
        Toast.makeText(getApplicationContext(), text, 1000).show();
    }

}
