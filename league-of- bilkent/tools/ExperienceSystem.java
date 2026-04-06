package tools;

import model.*;

import java.awt.Color;

/*
 * ┌──────────────────────────────────────────────────────────────┐
 * │               <<class>> ExperienceSystem                    │
 * │       XP/Tier utility (delegates to AppConstants)           │
 * ├──────────────────────────────────────────────────────────────┤
 * │ + getTierName(xp): String -> delegates to AppConstants      │
 * │ + getTierColor(xp): Color -> delegates to AppConstants      │
 * │ + getTierIndex(xp): int -> delegates to AppConstants        │
 * ├──────────────────────────────────────────────────────────────┤
 * │ USES:    AppConstants                                       │
 * │ NOTE:    Thin wrapper, most code calls AppConstants directly │
 * └──────────────────────────────────────────────────────────────┘
 */
public class ExperienceSystem {

    // Returns the tier name associated with the given XP value
    public static String getTierName(int xp){ 
        return AppConstants.getTierName(xp); 
    }
    
    // Returns the tier color associated with the given XP value
    public static Color getTierColor(int xp){
        return AppConstants.getTierColor(xp); 
    }

    // Returns the tier index associated with the given XP value
    public static int getTierIndex(int xp){
        return AppConstants.getTierIndex(xp); 
    }

}
