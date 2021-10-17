/*
 * Library of functions useful to multiple classes.
 */
package cds.utils;

import java.util.*;

import com.google.gson.JsonArray;

public class Functions {

	public static HashSet<Integer> jsonArrayToSet(JsonArray array) {
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < array.size(); i++)
			set.add(array.get(i).getAsInt());
		return set;
	}


}
