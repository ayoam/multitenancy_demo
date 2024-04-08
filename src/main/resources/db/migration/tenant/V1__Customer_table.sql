CREATE TABLE customer (
  id      BIGINT PRIMARY KEY,
  name    VARCHAR(255),
  type    VARCHAR(255)
);

-- CREATE SCHEMA TEST_SCHEMA;
--
-- CREATE TABLE TEST_SCHEMA.customer (
--     id      UUID PRIMARY KEY,
--     name    VARCHAR(255),
--     type    VARCHAR(255)
-- );
--
-- INSERT INTO TEST_SCHEMA.customer (id, name, type)
-- VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'mark', 'individuel');
