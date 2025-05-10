package ch.andre601.vanillaplus.util;

import ch.andre601.vanillaplus.VanillaPlus;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TranslatorUtil{
    
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, String>>(){}.getType();
    private final Map<String, Map<String, String>> languages = new HashMap<>();
    
    private final VanillaPlus plugin;
    private final File langFolder;
    
    public TranslatorUtil(VanillaPlus plugin){
        this.plugin = plugin;
        this.langFolder = plugin.getDataPath().getParent().resolve("IAWAILA").resolve("lang").toFile();
    }
    
    public boolean loadLanguages(){
        if(!langFolder.exists() || !langFolder.isDirectory()){
            plugin.getSLF4JLogger().warn("Unable to load Languages from /plugins/IAWAILA/lang/");
            plugin.getSLF4JLogger().warn("It either doesn't exist or is not a folder.");
            return false;
        }
        
        File[] files = langFolder.listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".json"));
        if(files == null || files.length == 0){
            plugin.getSLF4JLogger().warn("Unable to load lang files. Couldn't retrieve any files from /plugins/IAWAILA/lang/");
            return false;
        }
        
        for(File file : files){
            try(BufferedReader reader = Files.newBufferedReader(file.toPath())){
                Map<String, String> map = gson.fromJson(reader, mapType);
                languages.put(file.getName().substring(0, file.getName().lastIndexOf('.')), map);
            }catch(IOException ex){
                plugin.getSLF4JLogger().warn("Encountered an IOException while loading language files.", ex);
                return false;
            }
        }
        
        plugin.getSLF4JLogger().info("Loaded {} languages!", languages.size());
        return true;
    }
    
    public String getTranslation(String lang, String key){
        plugin.getSLF4JLogger().warn("Lang: {}; Key: {}", lang, key);
        Map<String, String> translations = languages.get(lang);
        if(translations == null)
            return null;
        
        return translations.get(key);
    }
}
