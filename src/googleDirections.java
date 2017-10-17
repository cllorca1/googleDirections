import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class googleDirections {

    private ResourceBundle rb;


    public static void main (String[] args){

        File propFile = new File("directions.properties");
        ResourceBundle rb = null;
        try {
            FileInputStream inputStream = new FileInputStream(propFile);
            rb = new PropertyResourceBundle(inputStream);
            System.out.println("Property-file read");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Example

        DirectionQuery dq = new DirectionQuery(rb);




    }
}
