package team.awesome.quickbus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.Color;

import team.awesome.quickbus.bus.Arrival;

public class Parser {

    /**
     * Gets the raw HTML code from a URL  (thanks will)
     *
     * @param urlString
     * @return html response
     */
    public static String fetchHTML(String urlString) {
        try {

            URL url = new URL(urlString);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.addRequestProperty( // TODO: make relevant for android
                    "User-Agent", System.getProperty("http.agent")//"Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
            ); // Chrome/20 worked too

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
        String fullURL = baseURL + stopNumber;
        String rawHTML = fetchHTML(fullURL);
        return extractArrivals(rawHTML);
    }

    /**
     * Returns a list of the upcoming arrivals given the raw html of the page
     *
     * @param rawHTML the raw HTML for the metlink page
     * @return ordered list of arrival objects
     */
    public static List<Arrival> extractArrivals(String rawHTML) {
        if (rawHTML == null) throw new IllegalArgumentException("rawHTML is null");
        List<Arrival> arrivals = new ArrayList<Arrival>();
        String[] sections = rawHTML.split("<table><thead><tr><th>Service</th>");

        //cut off the end of last one
        sections[sections.length - 1] = sections[sections.length - 1].split("</tr></tbody></table></div>")[0];

        //ignoring first section break them into rows
        for (int sectionIndex = 1; sectionIndex < sections.length; sectionIndex++) {
            String[] entryStrings = sections[sectionIndex].split("</tr><tr");
            for (String entryString : entryStrings) {
                arrivals.add(Parser.parseArrival(entryString));
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
    public static void ensureCorrectDays(List<Arrival> arrivals) {
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
     * @param entryString the HTML text lump
     * @return an arrival
     */
    public static Arrival parseArrival(String entryString) {
        if (entryString == null) throw new IllegalArgumentException("entryString is null");
        //bus number
        int numberIndex = entryString.indexOf("data-code");
        String numberString = entryString.substring(numberIndex + 11, numberIndex + 14);
        int number = Integer.parseInt(numberString);

        //service name
        String serviceString = entryString.split("<td>")[1].split("</td>")[0];

        //time
        String timeString = entryString.split("<td class=\"time\">")[1].split("</td>")[0];
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
        int colorIndex = entryString.indexOf("background-color");
        String colorString = entryString.substring(colorIndex + 18, colorIndex + 25);
        // TODO: Android can't use java.awt.color. find some other way to store this color
        Color color = null; //new Color(Color.parseColor(colorString));

        return new Arrival(number, serviceString, time, color);
    }

    public static void main(String[] args) throws Exception {
        List<Arrival> arrivals = fetchStopInfo(4921);
        for (Arrival arrival : arrivals) {
            System.out.println(arrival);
        }

    }

    public void fail(String text) {
        System.err.println("An error occurred while parsing: " + text);
        System.exit(1);
    }
}