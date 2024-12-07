CREATE EXTENSION dblink;

DO $$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database
      WHERE datname = 'data_service'
   ) THEN
      PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE data_service');
END IF;
END
$$;