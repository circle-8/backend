package org.circle8.utils;

import java.util.ArrayList;
import java.util.List;

import org.circle8.entity.Punto;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.val;

public class Puntos {
	private static final Gson GSON = new Gson();

	public static double calculateDistance(Punto pointA, Punto pointB) {
		val earthRadiusKm = 6371.0;
		val lat1Rad = Math.toRadians(pointA.latitud);
		val lon1Rad = Math.toRadians(pointA.longitud);
		val lat2Rad = Math.toRadians(pointB.latitud);
		val lon2Rad = Math.toRadians(pointB.longitud);

		val dLat = lat2Rad - lat1Rad;
		val dLon = lon2Rad - lon1Rad;

		val a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
			+ Math.cos(lat1Rad) * Math.cos(lat2Rad)
			* Math.sin(dLon / 2) * Math.sin(dLon / 2);

		val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return earthRadiusKm * c;
	}

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
