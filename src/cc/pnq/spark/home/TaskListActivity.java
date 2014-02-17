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

import cc.pnq.spark.home.service.*;

public class TaskListActivity extends FragmentActivity
{
    private ListView lv;
    private ArrayList<CronTask> data       = new ArrayList<CronTask>();
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.tasks );
        fetchTasks();
    }

    private void fetchTasks()
    {
        CronTaskService.fetchTasks(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                if( result.optBoolean("success") ) {
                    data.clear();
                    JSONArray tasks = result.optJSONArray("cron_tasks");
                    if( tasks != null && tasks.length() > 0) {
                        for( int i=0; i< tasks.length() ; i++ ) 
                        {
                            JSONObject t     = tasks.optJSONObject( i );
                            CronTask task    = new CronTask();
                            task.id          = t.optInt( "id" );
                            task.action      = t.optString( "action" );
                            task.hour        = t.optInt( "hour" );
                            task.minute      = t.optInt( "minute" );
                            task.workdayOnly = t.optBoolean( "workday_only" );
                            task.enabled     = t.optBoolean( "enabled" );
                            data.add( task );
                        }
                    }
                    render();
                } else {
                    //TODO: error
                    msg("TODO: error handler");
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject response) {
                msg( "连接失败 :(" );
            }
        });

    }

    private void render()
    {
        if (data.size() > 0) {
            initList();
        } else {
            renderEmpty();
        }
    }

    private void renderEmpty()
    {
        msg("TODO: render empty");
    }

    private void initList()
    {
        adapter = new SimpleAdapter(
            this,
            getData(),
            R.layout.cron_task,
            getFieldDef(),
            getViewDef()
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = super.getView( position, convertView, parent );
                int index    = position;

                TextView tf = (TextView) rowView.findViewById( R.id.ct_time );
                initTextView( tf, position );

                ImageView img = (ImageView) rowView.findViewById( R.id.ct_action );
                initActionBtn( img, position );

                CompoundButton tgBtn = (CompoundButton) rowView.findViewById( R.id.ct_enabled );
                initEnableBtn( tgBtn, position );

                return rowView;
            }
        };

        lv = (ListView) findViewById( R.id.listview );
        lv.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                msg( "TODO: remove task #" + position );
                return true;
            }
        });
        lv.setAdapter( adapter );
    }

    private void initTextView( TextView tf, int position )
    {
        final CronTask task = data.get( position );
        tf.setText( task.getTime() );
        tf.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                final TextView tf = (TextView) v;
                TimePickerFragment fragment = new TimePickerFragment() {

                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        task.setTime( hour, minute );
                        CronTaskService.update( task );
                        tf.setText( task.getTime() );
                    }
                };
                fragment.setDefaultTime( task.hour, task.minute );
                fragment.show( getSupportFragmentManager(), "timePicker" );
            }
        });

    }

    private void initActionBtn( ImageView img, int position )
    {
        final CronTask task = data.get( position );
        img.setImageResource(  task.isTurnOn() ? R.drawable.switch_on : R.drawable.switch_off );

        img.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                ImageView img = (ImageView) v;
                task.toggleTurn();
                CronTaskService.update( task );
                img.setImageResource(  task.isTurnOn() ? R.drawable.switch_on : R.drawable.switch_off );
            }
        });
    }

    private void initEnableBtn( CompoundButton tgBtn, int position )
    {
        final CronTask task = data.get( position );
        tgBtn.setChecked( task.enabled );

        tgBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                CompoundButton btn = (CompoundButton) v;
                task.enabled       = btn.isChecked();
                CronTaskService.update( task );
            }
        });
    }

    private List<Map<String, Object>> getData()
    {
        int size = data.size();
        list.clear();
        for(int i=0; i<size; i++) {
            CronTask task           = data.get( i );
            Map<String, Object> map = new HashMap<String, Object>();

            map.put( "time",        task.getTime() );
            map.put( "hour",        task.hour );
            map.put( "minute",      task.minute );
            map.put( "action",      task.action );
            map.put( "workdayOnly", task.workdayOnly );
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

    public void onClickNewTask( View v )
    {
        CronTask task = new CronTask();
        task.persisted = false;
        CronTaskService.update( task );
        data.add( task );

        getData();
        adapter.notifyDataSetChanged();
    }

    private void msg( String text ) {
        Toast.makeText(getApplicationContext(), text, 1000).show();
    }

}
