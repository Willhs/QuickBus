package team.awesome.quickbus;
import android.graphics.Color;

import java.util.Calendar;


/**
 * Super crude arrival class
 * @author Chris 
 *
 */
public class Arrival {
	
	public final int number;
	public final String service; 
	public final Color color;

	private Calendar date;
	
	
	
	public Arrival(int number,String service,Calendar time,Color color){
		this.number = number;		
		this.service = service;
		this.date = time;
		this.color = color;
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
		return "number "+number+" to "+service+" at "+date.getTime().toString();
	}

	
}
