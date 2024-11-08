package me.findthepeach.findyourpet.service;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

public interface GeocodingService {
    String calculateAddressFromLocation(Point<G2D> location);
}
