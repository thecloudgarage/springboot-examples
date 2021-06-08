-- Adminer 4.8.1 PostgreSQL 9.5.25 dump

DROP TABLE IF EXISTS "gpsinventory";
DROP SEQUENCE IF EXISTS gpsinventory_id_seq;
CREATE SEQUENCE gpsinventory_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 4 CACHE 1;

CREATE TABLE "public"."gpsinventory" (
    "id" bigint DEFAULT nextval('gpsinventory_id_seq') NOT NULL,
    "customer_id" character varying(255),
    "customer_name" character varying(255),
    "gpsterminal_id" character varying(255),
    "lat" double precision,
    "lon" double precision,
    CONSTRAINT "gpsinventory_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "gpsinventory" ("id", "customer_id", "customer_name", "gpsterminal_id", "lat", "lon") VALUES
(1,	'1',	'ABC001 limited',	'gps-01-001',	0,	0),
(2,	'2',	'ABC002 limited',	'gps-01-002',	0,	0),
(3,	'3',	'ABC003 limited',	'gps-01-003',	0,	0),
(4,	'4',	'ABC004 limited',	'gps-01-004',	0,	0);

-- 2021-06-08 07:01:59.003644+00
