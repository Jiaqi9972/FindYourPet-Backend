package me.findthepeach.findyourpet.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.geolatte.geom.Point;
import org.geolatte.geom.G2D;

import java.io.IOException;

public class PointSerializer extends StdSerializer<Point<G2D>> {

    public PointSerializer() {
        super((Class<Point<G2D>>) null);
    }

    @Override
    public void serialize(Point<G2D> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("longitude");
        gen.writeNumber(value.getPosition().getLon());
        gen.writeFieldName("latitude");
        gen.writeNumber(value.getPosition().getLat());
        gen.writeEndObject();
    }
}