import java.util.Arrays;
import java.util.HashSet;

public class MainService {

    public static void main(String[] args){
        String imageUrl = "https://i.imgur.com/Oh1JS09.png";
        OCRManager ocr = new OCRManager();
        DBManager db = new DBManager();
        try{
            String response = ocr.sendOCRPost(imageUrl);
            String parseResult = parseLicensePlate(response);
            if(parseResult.equals("approved")){
                System.out.println("The car with license plate "+ response+"was approved for entering the parking lot.");
            }
            else{
                System.out.println("The car with license plate "+ response+"was denied for entering the parking lot. The reason was: "+parseResult);
            }
        }catch(OCRException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

    private static String parseLicensePlate(String licensePlate){
        String lastTwo = licensePlate.substring(licensePlate.length()-3,licensePlate.length()-1);
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
