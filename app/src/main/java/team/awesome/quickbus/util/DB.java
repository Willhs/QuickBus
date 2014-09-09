package team.awesome.quickbus.util;

import java.util.HashSet;
import java.util.Set;

import team.awesome.quickbus.bus.BusStop;

/**
 * Created by will on 10/07/2014.
 *
 * Represents a database. Used to store favourite bus stops and services
 */
public class DB {

    private static Set<BusStop> favStops = new HashSet<BusStop>();
    private static Set<Integer> favServices = new HashSet<Integer>();// e.g. 1 (Island Bay <-> Station)

    public static Set<BusStop> getFavStops() { return favStops; }
    public static Set<Integer> getFavServices() { return favServices; }

    public static boolean addFavStop(BusStop stop){ return favStops.add(stop); }
    public static boolean addFavService(int service){ return favServices.add(service); }
    public static boolean removeFavStop(BusStop stop){ return favStops.remove(stop); }
    public static boolean removeFavService(int service){ return favServices.remove(service); }
}
