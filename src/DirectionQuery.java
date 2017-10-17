import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.joda.time.DateTime;

import java.util.ResourceBundle;

public class DirectionQuery {


        private TransitRoutingPreference transitRouting;
        private TravelMode travelMode;
        private ResourceBundle rb;

        public DirectionQuery(ResourceBundle rb, TravelMode travelMode, TransitRoutingPreference transitRouting) {
            this.transitRouting = transitRouting;
            this.travelMode = travelMode;
            this.rb=rb;
        }

        public DirectionsResult getResult(String origin, String destination, DateTime timeOfDay){
            GeoApiContext context = new GeoApiContext().setApiKey(rb.getString("api.key"));
            DirectionsResult directionsResult = new DirectionsResult();

            try {

                directionsResult = DirectionsApi.newRequest(context)
                        .units(Unit.METRIC)
                        .mode(travelMode)
                        .transitRoutingPreference(transitRouting)
                        .origin(origin)
                        .departureTime(timeOfDay)
                        .destination(destination).await();

            } catch (Exception e) {
                e.printStackTrace();
            }



            return directionsResult;

        }
}

