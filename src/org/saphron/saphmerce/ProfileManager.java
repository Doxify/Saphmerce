package org.saphron.saphmerce;

import java.util.HashMap;
import java.util.UUID;

public class ProfileManager {

    private HashMap<UUID, Profile> profiles = new HashMap<>();

    public Profile getProfile(UUID id) {
        if(profiles.containsKey(id)) {
            return profiles.get(id);
        }
        return null;
    }

    public boolean addProfile(UUID id) {
        if(!profiles.containsKey(id)) {
            profiles.put(id, new Profile());
            return true;
        }
        return false;
    }

    public boolean removeProfile(UUID id) {
        if(profiles.containsKey(id)) {
            profiles.remove(id);
            return true;
        }
        return false;
    }

    public void handleServerClose() {
        profiles.clear();
    }

}
