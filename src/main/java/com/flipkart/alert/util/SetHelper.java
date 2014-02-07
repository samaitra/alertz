package com.flipkart.alert.util;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 17/1/13
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetHelper {
    public static boolean setMatches(Set origSet, Set withSet) {
        boolean matches = true;
        if(origSet != null) {
            if(withSet == null)
                matches = false;
            else {
                if(origSet.size() != withSet.size())
                    matches = false;
                else {
                    for(Object orig : origSet) {
                        boolean found = false;
                        for(Object with : withSet) {
                            if(orig.equals(with)) {
                                found = true;
                                break;
                            }
                        }
                        if(!found) {
                            matches = false;
                            break;
                        }

                    }
                }
            }
        }
        else {
            matches = (withSet == null);
        }

        return matches;
    }

}
