package team.awesome.quickbus;

import java.util.Set;

import team.awesome.quickbus.bus.BusStop;

/**
 * Created by will on 10/07/2014.
 *
 * Represents a database. Used to store favourite bus stops and services
 */
public class DB {

    private static Set<BusStop> favStops;
    private static Set<Integer> favServices; // e.g. 1 (Island Bay <-> Station)

    public static Set<BusStop> getFavStops() {
        return favStops;
    }
    public static Set<Integer> getFavServices() {
        return favServices;
    }
    public static boolean addFavStop(BusStop bs){
        return favStops.add(bs);
    }
    public static boolean addFavService(int service){
        return favServices.add(service);
    }
    public static boolean removeFavStop(BusStop bs){
        return favStops.remove(bs);
    }
    public static boolean removeFavService(int service){
        return favServices.remove(service);
    }
}
