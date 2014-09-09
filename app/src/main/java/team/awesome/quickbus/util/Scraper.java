package team.awesome.quickbus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import team.awesome.quickbus.bus.Arrival;

/**
 * Retrieves data from online (currently metlink.co.nz)
 * AND Parses retrieved data so as to return in a useful format
 */
public class Scraper {

    /**
     * Gets the raw HTML code from a URL  (thanks will)
     *
     * @param urlString
     * @return html response
     */
    private static String fetchHTML(String urlString) {
        try {

            URL url = new URL(urlString);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.addRequestProperty( // Has been changed to be relevant for android
                    "User-Agent", System.getProperty("http.agent")//"Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
            );

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));


            String line = null;
            String returnString = "";
            while ((line = in.readLine()) != null) {
                returnString += line + "\n";
            }

            return returnString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Arrival> fetchStopInfo(int stopNumber) {
        String baseURL = "http://www.metlink.org.nz/stop/";
        String fullURL = baseURL + stopNumber + "/departures";
        String rawHTML = fetchHTML(fullURL);
        /*// TODO: change this temp code:
        List<Arrival> arrivals = new ArrayList<Arrival>();
        arrivals.add(new Arrival(4, rawHTML, null, "notcolourstring"));
        return arrivals;*/
        //return extractSchedArrivals(rawHTML);
        return extractLiveArrivals(rawHTML);
    }

    /**
     * Extracts live arrivals from an html string from metlink.co.nz/stop/(number)/departures
     * @param rawHTML
     * @return
     */
    private static List<Arrival> extractLiveArrivals(String rawHTML) {
        List<Arrival> arrivals = new ArrayList<Arrival>();
        // pattern which defines a string containing info about an arrival.
        String arrivalRegex = "\\<tr data-code\\=\"\\d+\" class\\=\"data\"\\>.*?\\</tr\\>";
        Matcher arrivalMatcher = Pattern.compile(arrivalRegex, Pattern.DOTALL).matcher(rawHTML);

        while (arrivalMatcher.find()) {
            arrivals.add(Scraper.parseLiveArrival(arrivalMatcher.group()));
        }
        return arrivals;
    }

    /**
     * Returns a list of the upcoming arrivals given the raw html of the page
     * TODO: Fix/remove this method
     *
     * @param rawHTML the raw HTML for the metlink page
     * @return ordered list of arrival objects
     */
    private static List<Arrival> extractSchedArrivals(String rawHTML) {
        List<Arrival> arrivals = new ArrayList<Arrival>();
        String[] sections = rawHTML.split("<table><thead><tr><th>Service</th>");

        //cut off the end of last one
        // TODO: cut off last one (not two) or make nicer overall
        sections[sections.length - 1] = sections[sections.length - 1].split("</tr></tbody></table></div>")[0];

        //ignoring first section break them into rows
        for (int sectionIndex = 1; sectionIndex < sections.length; sectionIndex++) {
            String[] entryStrings = sections[sectionIndex].split("</tr><tr");
            for (String entryString : entryStrings) {
                arrivals.add(Scraper.parseSchedArrival(entryString));
            }

        }

        //ensure date ordering correct
        ensureCorrectDays(arrivals);
        return arrivals;

    }

    /**
     * Methods cycles through the dates of the arrivals and (assuming they are ordered)
     * ensures the dates are correct
     *
     * @param arrivals
     */
    private static void ensureCorrectDays(List<Arrival> arrivals) {
        Calendar now = GregorianCalendar.getInstance();

        int ampm = now.get(Calendar.AM_PM);
        int daysPassed = 0;
        for (Arrival arr : arrivals) {
            //if switch
            Calendar date = arr.getDate();
            if (date.get(Calendar.AM_PM) != ampm) {
                ampm = date.get(Calendar.AM_PM);
                if (ampm == Calendar.AM) {
                    daysPassed++;
                }
            }

            //add on necessary days
            date.add(GregorianCalendar.DATE, daysPassed);

            //put date back in arrival
            arr.setDate(date);
        }
    }

    /**
     * From a lump of HTML text get the information for an arrival out
     *
     * @param arrivalHTML the HTML text lump
     * @return an arrival
     */
    private static Arrival parseSchedArrival(String arrivalHTML) {
        if (arrivalHTML == null) throw new IllegalArgumentException("entryString is null");
        //bus number
        int serviceIdStartIndex = arrivalHTML.indexOf("\"");
        String numberString = arrivalHTML.substring(serviceIdStartIndex, serviceIdStartIndex + 3);
        int serviceId = Integer.parseInt(numberString);

        //service name
        String serviceString = arrivalHTML.split("<td>")[1].split("</td>")[0];

        //time
        String timeString = arrivalHTML.split("<td class=\"time\">")[1].split("</td>")[0];
        int hours = Integer.parseInt(timeString.split(":")[0]);
        int mins = Integer.parseInt(timeString.split(":")[1].split("am|pm")[0]);
        boolean isPm;
        if (timeString.contains("pm")) {
            isPm = true;
        } else {
            isPm = false;
        }
        Calendar time = GregorianCalendar.getInstance();
        time.set(GregorianCalendar.HOUR, hours);
        time.set(GregorianCalendar.MINUTE, mins);
        if (isPm) {
            time.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);
        } else {
            time.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        }

        //color
        int colorIndex = arrivalHTML.indexOf("background-color");
        String colourString = arrivalHTML.substring(colorIndex + 18, colorIndex + 25);
        // TODO: Android can't use java.awt.color. find some other way to store this color

        return new Arrival(serviceId, serviceString, time, colourString);
    }

    /**
     * From a lump of HTML text get the information for a *live* arrival out
     * **NOTE**: Rather hacky! if Matcher.find() doesn't return true: parser wrong or metlink changed code
     *
     * @param arrivalHTML the HTML text lump
     * @return an arrival
     */
    public static Arrival parseLiveArrival(String arrivalHTML){
        if (arrivalHTML == null) throw new IllegalArgumentException("arrival HTML string is null");

        //bus id
        String idRegex = "data-code\\=\"\\d+\"";
        Matcher idMatcher = Pattern.compile(idRegex).matcher(arrivalHTML);
        if (!idMatcher.find()) {
            Log.e("couldn't find id in html", arrivalHTML);
        }
        String idString = idMatcher.group();
        String numberString = idString.substring(idString.indexOf("\"")+1, idString.lastIndexOf("\""));
        int id = Integer.parseInt(numberString);

        //service name
        String serviceNameRegex = "\\<span\\>.+\\</span\\>";
        Matcher serviceNameMatcher = Pattern.compile(serviceNameRegex).matcher(arrivalHTML);
        if (!serviceNameMatcher.find()) {
            Log.e("couldn't find service name in html", arrivalHTML);
        }
        String serviceString = serviceNameMatcher.group();
        String serviceName = serviceString.substring(serviceString.indexOf(">")+1, serviceString.lastIndexOf("<")).trim();

        //time till arrival
        String timeRegex = "\\<span class\\=\"rt-service-time.*\".+\\>.+\\</span\\>";
        Matcher timeMatcher = Pattern.compile(timeRegex, Pattern.DOTALL).matcher(arrivalHTML);
        if (!timeMatcher.find()) {
            Log.e("couldn't find time in html", arrivalHTML);
        }
        String timeString = timeMatcher.group();
        String time = timeString.substring(timeString.indexOf(">")+1, timeString.lastIndexOf("<")).trim();

        //color
        String colourRegex = "background-color: #.+\"";
        Matcher colourMatcher = Pattern.compile(colourRegex).matcher(arrivalHTML);
        if (!colourMatcher.find()) {
            Log.e("couldn't find colour in html", arrivalHTML);
        }

        String colourString = colourMatcher.group();
        String colour = colourString.substring(colourString.indexOf("#"), colourString.indexOf("\""));

        return new Arrival(id, serviceName, time, colour);
    }

    public static void main(String[] args) {
        List<Arrival> arrivals = fetchStopInfo(4921);
        for (Arrival arrival : arrivals) {
            System.out.println(arrival);
        }

    }

    private static void fail(String text) {
        System.err.println("An error occurred while parsing: " + text);
        System.exit(1);
    }
}