package me.findthepeach.findyourpet.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import me.findthepeach.findyourpet.utils.serializer.PointDeserializer;
import me.findthepeach.findyourpet.utils.serializer.PointSerializer;
import org.geolatte.geom.G2D;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.geolatte.geom.Point;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lost_pets", indexes = {
        @Index(name = "idx_lost_pet_location", columnList = "location", unique = false)
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LostPet {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "pet_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    private String description;

    private String posterContact;

    @JsonSerialize(using = PointSerializer.class)
    @JsonDeserialize(using = PointDeserializer.class)
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point<G2D> location;

    @Type(JsonType.class)
    @Column(name = "pet_image_urls", columnDefinition = "JSONB")
    private List<String> petImageUrls;

    // true means pet was found or pet was sent to the owner
    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    // Can be used for both lost or found date
    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

    // true means user lost a pet
    // false means user found a pet
    @Column(name = "lost", nullable = false)
    private Boolean lost;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "edited_on")
    private OffsetDateTime editedOn;
}
