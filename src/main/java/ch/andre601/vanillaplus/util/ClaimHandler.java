package ch.andre601.vanillaplus.util;

import ch.andre601.vanillaplus.VanillaPlus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ClaimHandler{
    
    private final Type mapType = new TypeToken<Map<String, List<Claim>>>(){}.getType();
    private final Gson gson = new Gson();
    
    private final Map<String, List<Claim>> claims = new HashMap<>();
    private final List<String> iaFlags = List.of(
        "ia-furniture-sit",
        "ia-placed-block-interact",
        "ia-placed-furniture-interact"
    );
    
    private final VanillaPlus plugin;
    private final Path claimsFile;
    
    public ClaimHandler(VanillaPlus plugin){
        this.plugin = plugin;
        this.claimsFile = plugin.getDataPath().resolve("claims.json");
    }
    
    public boolean load(){
        if(!Files.exists(claimsFile)){
            plugin.getSLF4JLogger().info("claims.json not found. Skipping...");
            return true;
        }
        
        claims.clear();
        
        try(JsonReader reader = new JsonReader(new FileReader(claimsFile.toFile()))){
            Map<String, List<Claim>> map = gson.fromJson(reader, mapType);
            if(map == null){
                plugin.getSLF4JLogger().warn("Unable to load JSON data from claims.json file!");
                return false;
            }
            
            claims.putAll(map);
            plugin.getSLF4JLogger().info("Loaded {} Users with claims.", claims.size());
            return true;
        }catch(IOException ex){
            plugin.getSLF4JLogger().warn("Encountered an IOException while loading the file.", ex);
            return false;
        }
    }
    
    public boolean save(){
        try{
            String json = gson.toJson(claims);
            InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            Files.copy(stream, claimsFile, StandardCopyOption.REPLACE_EXISTING);
            
            plugin.getSLF4JLogger().info("Saved {} Player(s) to claims.json file.", claims.size());
            return true;
        }catch(IOException ex){
            plugin.getSLF4JLogger().warn("Encountered IOException while saving to claims.json!", ex);
            return false;
        }
    }
    
    public void claim(Player player){
        if(!hasClaimsLeft(player)){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>You cannot claim any more chunks!"));
            return;
        }
        
        ItemStack[] items = player.getInventory().getContents();
        ItemStack item = null;
        for(ItemStack stack : items){
            CustomStack customStack = CustomStack.byItemStack(stack);
            if(customStack != null && customStack.getNamespacedID().equals("vanillaplus:claim_permit")){
                item = stack;
                break;
            }
        }
        
        if(item == null){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>You need a <grey>Claim Permit</grey> to claim an Area!"));
            player.sendMessage(VanillaPlus.MM.deserialize("<red>You can get one from the <grey>Claim Manager</grey>."));
            return;
        }
        
        RegionManager manager = WorldGuardHandler.getRegionManager(player.getWorld());
        if(manager == null){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>Couldn't obtain RegionManager. If this issue persists, report it!"));
            return;
        }
        
        Claim claim = Claim.create(player.getChunk());
        ProtectedCuboidRegion region = claim.asCuboidRegion(player.getWorld());
        
        if(!WorldGuardHandler.canClaim(manager, region)){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>Your desired Claim is overlapping with another Region."));
            return;
        }
        
        region.setFlags(getFlags(claim, player));
        
        DefaultDomain members = region.getMembers();
        members.addPlayer(player.getUniqueId());
        
        region.setMembers(members);
        
        manager.addRegion(region);
        
        List<Claim> claims = this.claims.get(player.getUniqueId().toString());
        if(claims == null)
            claims = new ArrayList<>();
        
        claims.add(claim);
        
        this.claims.put(player.getUniqueId().toString(), claims);
        
        player.sendMessage(VanillaPlus.MM.deserialize(String.format(
            "<green><hover:show_text:\"%s\">Successfully claimed Region</hover>",
            claim.toMMString()
        )));
        
        item.setAmount(item.getAmount() - 1);
    }
    
    public List<Claim> claims(Player player){
        return claims.get(player.getUniqueId().toString());
    }
    
    public Claim claimById(String id){
        for(List<Claim> claims : claims.values()){
            for(Claim claim : claims){
                if(claim.regionId().equals(id))
                    return claim;
            }
        }
        
        return null;
    }
    
    public List<Claim> allClaims(){
        List<Claim> allClaims = new ArrayList<>();
        for(List<Claim> claims : claims.values()){
            allClaims.addAll(claims);
        }
        
        return allClaims;
    }
    
    public boolean hasClaim(String id){
        return claimById(id) != null;
    }
    
    public void removeClaim(String id){
        for(List<Claim> claims : claims.values()){
            claims.removeIf(claim -> claim.regionId().equals(id));
        }
    }
    
    private boolean hasClaimsLeft(Player player){
        if(!claims.containsKey(player.getUniqueId().toString()))
            return true;
        
        List<Claim> chunkAreas = claims.get(player.getUniqueId().toString());
        if(chunkAreas.isEmpty())
            return true;
        
        return player.hasPermission("vanillaplus.claims.limit." + (chunkAreas.size() + 1));
    }
    
    private Map<Flag<?>, Object> getFlags(Claim claim, Player player){
        Map<Flag<?>, Object> flags = new HashMap<>();
        
        flags.put(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
        flags.put(Flags.PASSTHROUGH.getRegionGroupFlag(), RegionGroup.MEMBERS);
        
        // Deprecated but no alternative is available.
        //noinspection deprecation
        flags.put(Flags.GREET_TITLE, String.format(
            ChatColor.GREEN + "%s\\nClaim of %s",
            claim.regionId(),
            player.getName()
        ));
        //noinspection deprecation
        flags.put(Flags.DENY_MESSAGE, ":alert: &7This area is &cprotected&7! You can't %what% here.");
        
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        for(String iaFlag : iaFlags){
            Flag<?> flag = registry.get(iaFlag);
            if(!(flag instanceof StateFlag stateFlag))
                continue;
            
            flags.put(stateFlag, StateFlag.State.ALLOW);
            flags.put(stateFlag.getRegionGroupFlag(), RegionGroup.MEMBERS);
        }
        
        return flags;
    }
}
