package me.findthepeach.findyourpet.utils.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometries;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystems;

import java.io.IOException;

public class PointDeserializer extends StdDeserializer<Point<G2D>> {

    public PointDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public Point<G2D> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        double lon = node.get("longitude").asDouble();
        double lat = node.get("latitude").asDouble();

        return Geometries.mkPoint(new G2D(lon, lat), CoordinateReferenceSystems.WGS84);
    }
}