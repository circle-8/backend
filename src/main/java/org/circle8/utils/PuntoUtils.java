package org.circle8.utils;

import java.util.ArrayList;
import java.util.List;

import org.circle8.entity.Punto;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.val;

public class PuntoUtils {
	private static final Gson GSON = new Gson();

	public static List<Punto> getPolyline(String poly) {
		val l = new ArrayList<Punto>();
		if(!Strings.isNullOrEmpty(poly)) {
			float[][] list = GSON.fromJson(poly, float[][].class);
			for (float[] element : list) {
				l.add(new Punto(element[0], element[1]));
			}
		}		
		return l;
	}
}
