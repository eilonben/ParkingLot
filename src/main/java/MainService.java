import java.util.Arrays;
import java.util.HashSet;

public class MainService {

    public static void main(String[] args){
        if(args.length<1){
            System.out.println("please enter the file path.");
        }
        else {
            String path = args[0];
            String format = path.substring(path.lastIndexOf('.') + 1);
            OCRManager ocr = new OCRManager();
            DBManager db = new DBManager();
            try {
                String response = ocr.sendOCRPost(path, format);
                if (response != null) {
                    String plate = response.trim();
                    String parseResult = parseLicensePlate(plate);
                    db.insert(plate, parseResult);
                    if (parseResult.equals("approved")) {
                        System.out.println("The car with license plate " + plate + " was approved for entering the parking lot.");
                    } else {
                        System.out.println("The car with license plate " + plate + " was denied for entering the parking lot. The reason was: " + parseResult);
                    }
                }
            } catch (OCRException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the car with the license plate number given is approved for entering the parking lot
     * Returns a string - "approved" / a reason for denial of entrance
     * @param textGiven the license plate to be checked
     */
    private static String parseLicensePlate(String textGiven){
        String licensePlate = textGiven.replace("-","");
        if(licensePlate.length()<2){
            return "approved";
        }
        String lastTwo = licensePlate.substring(licensePlate.length()-2);
        HashSet<String> publicT = new HashSet<>(Arrays.asList("25","26"));
        HashSet<String> prohibited = new HashSet<>(Arrays.asList("85","86","87","88","89","00"));
        if(publicT.contains(lastTwo))
            return "Public Transport";
        if(licensePlate.length()==7  && prohibited.contains(lastTwo))
            return "General";
        int sum = licensePlate
                .chars()
                .map(Character::getNumericValue)
                .sum();
        if(sum % 7 == 0) {
            return "Gas Operated";
        }
        if(licensePlate.matches("(?s).*[a-zA-Z]+(?s).*")) {
            return "Military&Law";
        }
        return "approved";
    }
}
