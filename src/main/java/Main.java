import Classes.Champion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;


public class Main {
    public static void main(String[] args) {
        //Json Parser
        ObjectMapper objectMapper = new ObjectMapper();
        //Data Structure
        ArrayList<Champion> championObjects = new ArrayList<Champion>();
        ArrayList<String> champList = new ArrayList<String>();
        try{
            //Http Client
            HttpClient client = HttpClient.newHttpClient();
            //Create Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ddragon.leagueoflegends.com/cdn/14.6.1/data/en_US/champion.json"))
                    .build();
            //Respond received
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //Respond's content into Strings
            String responseBody = response.body();
            //Json Parsing using Jackson
            JsonNode rootNode = objectMapper.readTree(responseBody);
            //Access the field: Data, to extract champion name
            JsonNode dataNode = rootNode.path("data");
            //JsonNode method
            Iterator<String> iterator = dataNode.fieldNames();
            //Extraction of Champion Name
            while (iterator.hasNext()) {
                String fieldName = iterator.next(); // This is the variable name, e.g., "Aatrox", "Ahri"
                champList.add(fieldName);
                System.out.println(fieldName);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        ;

        //Champion Stats Extraction
        String urlTemplate = "https://raw.communitydragon.org/14.5/game/data/characters/";
        for(String champName : champList){
            String urlInsert = champName.toLowerCase();
            String actualURL = urlTemplate+urlInsert+"/"+urlInsert+".bin.json";
            //System.out.println(actualURL);
           // String url = "https://raw.communitydragon.org/14.5/game/data/characters/aatrox/aatrox.bin.json";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(actualURL))
                    .build();
            try {
                //Http response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                //Json string
                String responseBody = response.body();
                // Here we just parse it to a JSONObject to validate and print, in reality, you might want to save this.
                JSONObject json = new JSONObject(responseBody);
                //JsonNode object parsing
                JsonNode rootNode = objectMapper.readTree(responseBody);
                // Attribute Extraction
                //String modifiedName = champName.substring(0,1).toUpperCase() + champName.substring(1);;
                String pathName = "Characters/"+champName+"/CharacterRecords/Root";
                JsonNode name = rootNode.path(pathName).path("mCharacterName");
                JsonNode baseHP = rootNode.path(pathName).path("baseHP");
                JsonNode hpPerLevel = rootNode.path(pathName).path("hpPerLevel");
                JsonNode baseDamage = rootNode.path(pathName).path("baseDamage");
                JsonNode damagePerLevel = rootNode.path(pathName).path("damagePerLevel");
                JsonNode baseArmor = rootNode.path(pathName).path("baseArmor");
                JsonNode armorPerLevel = rootNode.path(pathName).path("armorPerLevel");
                JsonNode baseSpellBlock = rootNode.path(pathName).path("baseSpellBlock");
                JsonNode spellBlockPerLevel = rootNode.path(pathName).path("spellBlockPerLevel");
                JsonNode baseMoveSpeed = rootNode.path(pathName).path("baseMoveSpeed");
                JsonNode attackSpeed = rootNode.path(pathName).path("attackSpeed");
                JsonNode attackSpeedRatio = rootNode.path(pathName).path("attackSpeedRatio");
                JsonNode attackSpeedPerLevel = rootNode.path(pathName).path("attackSpeedPerLevel");

                //build ability class:
                //Step 1: find all spell, then find passive
                //path for passive:
                //to look for multi stage spell: mChildSpells
                //to look for specific spell: Characters/*champion name*/Spells/*champion name* + *slot* + Ability/ *champion name* + *slot*
                //design, extract path name for spell using : Spellnames and extract name, build into the
                Champion champ = new Champion(name.asText(),
                        baseHP.asDouble(), hpPerLevel.asDouble(),
                        baseDamage.asDouble(), damagePerLevel.asDouble(),
                        baseArmor.asDouble(), armorPerLevel.asDouble(),
                        baseSpellBlock.asDouble(),spellBlockPerLevel.asDouble(),
                        baseMoveSpeed.asDouble(),
                        attackSpeed.asDouble(), attackSpeedRatio.asDouble(),attackSpeedPerLevel.asDouble());
                championObjects.add(champ);
                //System.out.println(json.toString(4)); // Print with indentation for readability

                // Here's where you'd save the json.toString() to a file or database as needed.
                // For example, to write to a file:
                // Files.writeString(Paths.get("output.json"), json.toString(4));

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Test
        for(Champion c: championObjects){
            System.out.println(c.name);
        }
    }
}