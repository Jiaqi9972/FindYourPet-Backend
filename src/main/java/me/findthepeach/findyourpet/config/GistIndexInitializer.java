package me.findthepeach.findyourpet.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GistIndexInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public GistIndexInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // delete existed B-Tree index (if exists)
        jdbcTemplate.execute("DROP INDEX IF EXISTS idx_lost_pet_location");

        // create GIST index
        jdbcTemplate.execute("CREATE INDEX idx_lost_pet_location ON lost_pets USING GIST (location)");
    }
}