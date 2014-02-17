package cc.pnq.spark.home;

public class CronTask
{
    public long id;
    public int hour;
    public int minute;
    public Boolean persisted   = true;
    public String action       = "turn_on";
    public Boolean workdayOnly = false;
    public Boolean enabled     = true;

    public String toString()
    {
        return "#<CronTask " + hour
            + ":" + minute 
            + ( workdayOnly ? " (week day only)" : "" ) 
            + ( enabled ? "" : " ( disabled )" )
            + ">";
    }

    public String getTime() {
        return strf(hour) + ":" + strf(minute);
    }

    private String strf( int v ) {
        return v > 9 ? String.valueOf(v) : "0" + v;
    }

    public void setTime( int hour, int minute ) {
        this.hour = hour;
        this.minute = minute;
    }

    public Boolean isTurnOn() {
        return action.equals( "turn_on" );
    }

    public void toggleTurn() {
        if( isTurnOn() ) {
            action = "turn_off";
        } else {
            action = "turn_on";
        }
    }

}
