package team.awesome.quickbus.bus;

/**
 * Created by will on 10/07/2014.
 * BusStop, used to store as user favourite in DB
 */
public class BusStop {
    private String address;
    private int id; // aka stop number

    public BusStop(String address, int id){
        this.address = address;
        this.id = id;
    }

    public String getAddress(){
        return address;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        return address;
    }
}
