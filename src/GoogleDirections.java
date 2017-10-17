import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.*;
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

        DirectionQuery dq = new DirectionQuery(rb);

        try {
            DirectionsResult directionsResult = dq.getResult("Augustenstr. 44, Munich", "Ostbahnhof, Munich", new DateTime(2017, 11, 10, 8, 0, 0));

            logger.info("There is (are) " + directionsResult.routes.length + " different route(s)");

            for(int k = 0; k < directionsResult.routes.length; k++) {

                for(int j = 0; j < directionsResult.routes[k].legs.length; j++) {
                    for(int i = 0; i < directionsResult.routes[k].legs[j].steps.length; i++) {
                        logger.info("route " + k + " leg " + j + " step " + i);
                        logger.info(directionsResult.routes[k].legs[j].steps[i].travelMode);
                        logger.info(directionsResult.routes[k].legs[j].steps[i].startLocation);
                        logger.info(directionsResult.routes[k].legs[j].steps[i].endLocation);
                        logger.info(directionsResult.routes[k].legs[j].steps[i].distance);
                        if (directionsResult.routes[k].legs[j].steps[i].travelMode.equals(TravelMode.TRANSIT)) {
                            logger.info(directionsResult.routes[k].legs[j].steps[i].transitDetails.departureStop.name);
                            logger.info(directionsResult.routes[k].legs[j].steps[i].transitDetails.arrivalStop.name);
                            logger.info(directionsResult.routes[k].legs[j].steps[i].transitDetails.line.shortName);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
