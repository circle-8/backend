package org.circle8.utils;

import org.circle8.entity.Punto;

import lombok.val;

public class PuntoUtils {

	
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
}
