package team.awesome.quickbus.bus;
import android.graphics.Color;

import java.util.Calendar;


/**
 * Super crude arrival class
 * @author Chris 
 *
 */
public class Arrival {
	
	private final int id;
	private final String service;
	private final String colour;

	private Calendar date;
    private String timeTillArrival;

	public Arrival(int id, String service, Calendar time, String colour){
		this.id = id;
		this.service = service;
		this.date = time;
		this.colour = colour;
	}

    public Arrival(int id, String service, String timeTillArrival, String colour){
        this.id = id;
        this.service = service;
        this.timeTillArrival = timeTillArrival;
        this.colour = colour;
    }

	public Calendar getDate() {
		Calendar copy = Calendar.getInstance(date.getTimeZone());
		copy.setTime(date.getTime());
		return  copy;
	}

	public void setDate(Calendar date) {
		this.date = date;		
	}

	public String toString(){
		return "id "+ getId() +" to "+ getService() +" at "+timeTillArrival;
	}

    public int getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getColour() { return colour; }

    public String getTimeTillArrival() {
        return timeTillArrival;
    }
}