import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.*;
import java.util.*;

public class Converter {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("C:\\Users\\L900m\\Downloads\\Walk.json")));
        JsonObject oldJson = JsonParser.parseString(content).getAsJsonObject();
        
        JsonObject newJson = new JsonObject();
        newJson.addProperty("format_version", "1.8.0");
        
        JsonObject animations = new JsonObject();
        JsonObject walkAnim = new JsonObject();
        walkAnim.addProperty("loop", oldJson.get("loop").getAsBoolean());
        walkAnim.addProperty("animation_length", oldJson.get("length").getAsFloat());
        
        JsonObject bones = new JsonObject();
        JsonArray oldAnims = oldJson.getAsJsonArray("animations");
        
        for (JsonElement el : oldAnims) {
            JsonObject part = el.getAsJsonObject();
            String boneName = part.get("bone").getAsString();
            String target = part.get("target").getAsString();
            
            if (!bones.has(boneName)) {
                bones.add(boneName, new JsonObject());
            }
            JsonObject bone = bones.getAsJsonObject(boneName);
            
            JsonObject targetObj = new JsonObject();
            JsonArray keyframes = part.getAsJsonArray("keyframes");
            for (JsonElement kfEl : keyframes) {
                JsonObject kf = kfEl.getAsJsonObject();
                float timestamp = kf.get("timestamp").getAsFloat();
                JsonArray targetArr = kf.getAsJsonArray("target");
                targetObj.add(String.valueOf(timestamp), targetArr);
            }
            bone.add(target, targetObj);
        }
        
        walkAnim.add("bones", bones);
        animations.add("animation.model.walk", walkAnim);
        newJson.add("animations", animations);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get("C:\\Users\\L900m\\Downloads\\Walk_fixed.json"), gson.toJson(newJson).getBytes());
    }
}
