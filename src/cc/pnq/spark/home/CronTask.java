package cc.pnq.spark.home;

public class CronTask
{

    public int hour;
    public int minute;
    public String action       = "turnOn";
    public Boolean weekdayOnly = false;
    public Boolean enabled     = true;

    public String toString()
    {
        return "#<CronTask " + hour
            + ":" + minute 
            + ( weekdayOnly ? " (week day only)" : "" ) 
            + ( enabled ? "" : " ( disabled )" )
            + ">";
    }

    public String getTime()
    {
        return hour + ":" + minute;
    }

    public void setTime( int hour, int minute )
    {
        this.hour = hour;
        this.minute = minute;
    }

}
