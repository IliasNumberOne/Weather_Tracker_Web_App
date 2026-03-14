ALTER TABLE user_locations
    ADD CONSTRAINT unique_user_location UNIQUE (user_id, location_id);