import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TransitRoutingPreference;
import com.google.maps.model.TravelMode;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class GoogleDirections {
    private ResourceBundle rb;
    private static Logger logger = Logger.getLogger(GoogleDirections.class);

    public GoogleDirections() {
    }

    public static void main(String[] args) {
        File propFile = new File("directions.properties");
        PropertyResourceBundle rb = null;

        try {
            FileInputStream inputStream = new FileInputStream(propFile);
            rb = new PropertyResourceBundle(inputStream);
            System.out.println("Property-file read");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        DirectionQuery dq = new DirectionQuery(rb, TravelMode.TRANSIT, TransitRoutingPreference.LESS_WALKING);


        String recString = "";
        int recCount = 0;

        try {

            PrintWriter pw = new PrintWriter(new FileWriter("outputTrips.csv", false));
            pw.println("id,from,stop1,stop2,stop3,destination,distance,steps");


            BufferedReader in = new BufferedReader(new FileReader("inputTrips.csv"));
            recString = in.readLine();

            String[] header = recString.split(",");

            int posId = findPositionInArray("id", header);
            int posDay = findPositionInArray("day", header);
            int posMonth = findPositionInArray("month", header);
            int posHour = findPositionInArray("hour", header);
            int posMin = findPositionInArray("minute", header);
            int posOrigin = findPositionInArray("origin", header);
            int posDest = findPositionInArray("destination", header);
            int posStop1 = findPositionInArray("stop1", header);
            int posStop2 = findPositionInArray("stop2", header);
            int posStop3 = findPositionInArray("stop3", header);

            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int day = Integer.parseInt(lineElements[posDay]);
                int month = Integer.parseInt(lineElements[posMonth]);
                int hour = Integer.parseInt(lineElements[posHour]);
                int minute = Integer.parseInt(lineElements[posMin]);
                String origin = lineElements[posOrigin];
                String destination = lineElements[posDest];
                String stop1 = lineElements[posStop1];
                String stop2 = lineElements[posStop2];
                String stop3 = lineElements[posStop3];

                //add a sequence of stops
                Map<Integer, String> completeRoute = new HashMap<>();

                int sequence = 0;
                completeRoute.put(sequence, origin);
                sequence++;

                if (!stop1.equals("")) {
                    completeRoute.put(sequence, stop1);
                    sequence++;
                }

                if (!stop2.equals("")) {
                    completeRoute.put(sequence, stop2);
                    sequence++;
                }

                if (!stop3.equals("")) {
                    completeRoute.put(sequence, stop3);
                    sequence++;
                }

                completeRoute.put(sequence, destination);

                pw.print(id + "," +
                        origin + "," +
                        stop1 + "," +
                        stop2 + "," +
                        stop3 + "," +
                        destination + ",");

                DateTime startTime = new DateTime(2017, month, day, hour , minute , 0);
                //adapt to new time zone?
                startTime.minusMinutes(3*60+30);

                int tripDuration = 0;
                long totalDistance = 0;
                String legSteps = "";

                for (int segment = 0; segment < sequence; segment++) {

                    String fromLocation = completeRoute.get(segment);
                    String toLocation = completeRoute.get(segment + 1);

                    //update starting time for the second segment and successives
                    startTime.plusSeconds(tripDuration);

                    DirectionsResult directionsResult = dq.getResult(fromLocation, toLocation, startTime);


                    for (int k = 0; k < directionsResult.routes.length; k++) {
                        //only 1 route in general

                        for (int j = 0; j < directionsResult.routes[k].legs.length; j++) {
                            //only 1 leg when using transit


                            for (int i = 0; i < directionsResult.routes[k].legs[j].steps.length; i++) {
                                //legSteps += "WITH";
                                //legSteps += directionsResult.routes[k].legs[j].steps[i].travelMode.toString();
                                //steps = transit lines or transit + walking segments
                                if (directionsResult.routes[k].legs[j].steps[i].travelMode.equals(TravelMode.TRANSIT)) {
                                    legSteps += "FROM";
                                    legSteps += directionsResult.routes[k].legs[j].steps[i].transitDetails.departureStop.name;
                                    legSteps += "TO";
                                    legSteps += directionsResult.routes[k].legs[j].steps[i].transitDetails.arrivalStop.name;
                                    //logger.info(directionsResult.routes[k].legs[j].steps[i].transitDetails.departureStop.name);
                                    //logger.info(directionsResult.routes[k].legs[j].steps[i].transitDetails.arrivalStop.name);
                                    ///logger.info(directionsResult.routes[k].legs[j].steps[i].transitDetails.line.shortName);
                                }
                                totalDistance += directionsResult.routes[k].legs[j].steps[i].distance.inMeters;
                            }
                            //logger.info(directionsResult.routes[k].legs[j].arrivalTime);
                            //logger.info("Total distance is: " + totalDistance);
                            tripDuration += directionsResult.routes[k].legs[j].duration.inSeconds;
                        }

                    }
                }
                pw.println(totalDistance + "," + legSteps);
            }

            pw.flush();
            pw.close();


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int findPositionInArray(String element, String[] arr) {
        // return index position of element in array arr
        int ind = -1;
        for (int a = 0; a < arr.length; a++) if (arr[a].equalsIgnoreCase(element)) ind = a;
        if (ind == -1) logger.error("Could not find element " + element +
                " in array (see method <findPositionInArray> in class <SiloUtil>");
        return ind;
    }
}
