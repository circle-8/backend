SET MODE PostgreSQL;

CREATE TYPE public."TipoUsuario" AS ENUM
    ('CIUDADANO', 'RECICLADOR_URBANO', 'ORGANIZACION', 'TRANSPORTISTA', 'RECICLADOR_PARTICULAR');

CREATE TABLE IF NOT EXISTS public."Ciudadano"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "UsuarioId" bigint NOT NULL,
    CONSTRAINT "Ciudadano_pkey" PRIMARY KEY ("ID"),
    CONSTRAINT "Ciudadano_UsuarioId_key" UNIQUE ("UsuarioId")
);

CREATE TABLE IF NOT EXISTS public."Organizacion"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "RazonSocial" character varying(50) NOT NULL,
    "UsuarioId" bigint NOT NULL,
    CONSTRAINT "Organizacion_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Plan"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "Nombre" character varying(50) NOT NULL,
    "Precio" numeric NOT NULL,
    "MesesRenovacion" integer NOT NULL,
    "CantUsuarios" integer NOT NULL,
    CONSTRAINT "Plan_pkey" PRIMARY KEY ("ID"),
    CONSTRAINT "UNIQUE_Name" UNIQUE ("Nombre")
);

CREATE TABLE IF NOT EXISTS public."PuntoReciclaje"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    "CiudadanoId" bigint,
    "Latitud" double precision NOT NULL,
    "Longitud" double precision NOT NULL,
    "DiasAbierto" character varying NOT NULL,
    "Titulo" character varying NOT NULL,
    CONSTRAINT "PuntoReciclaje_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."PuntoReciclaje_TipoResiduo"
(
    "PuntoReciclajeId" bigint NOT NULL,
    "TipoResiduoId" bigint NOT NULL,
    CONSTRAINT "PuntoReciclaje_TipoResiduo_pkey" PRIMARY KEY ("PuntoReciclajeId", "TipoResiduoId")
);

CREATE TABLE IF NOT EXISTS public."PuntoResiduo"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "CiudadanoId" bigint NOT NULL,
    "Latitud" double precision NOT NULL,
    "Longitud" double precision NOT NULL,
    CONSTRAINT "PuntoResiduo_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."RecicladorUrbano"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "UsuarioId" bigint NOT NULL,
    CONSTRAINT "RecicladorUrbano_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Recorrido"
(
    "ID" bigint NOT NULL,
    "FechaRetiro" date NOT NULL,
    "FechaInicio" timestamp with time zone,
    "FechaFin" time with time zone,
    "RecicladorId" bigint NOT NULL,
    "ZonaId" bigint NOT NULL,
    CONSTRAINT "Recorrido_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Residuo"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "FechaCreacion" timestamp with time zone NOT NULL,
    "FechaRetiro" timestamp with time zone,
    "PuntoResiduoId" bigint NOT NULL,
    "TipoResiduoId" bigint NOT NULL,
    "TransaccionId" bigint,
    "RecorridoId" bigint,
    "Descripcion" character varying NOT NULL,
    "FechaLimiteRetiro" timestamp with time zone,
    CONSTRAINT "Residuo_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Suscripcion"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "UltimaRenovacion" date NOT NULL,
    "ProximaRenovacion" date NOT NULL,
    "PlanId" bigint NOT NULL,
    CONSTRAINT "Suscripcion_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."TipoResiduo"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "Nombre" character varying NOT NULL,
    CONSTRAINT "TipoResiduo_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."TransaccionResiduo"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "FechaPrimerContacto" timestamp with time zone NOT NULL,
    "FechaEfectiva" timestamp with time zone,
    "PuntoReciclajeId" bigint NOT NULL,
    "TransporteId" bigint,
    CONSTRAINT "TransaccionResiduo_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Transporte"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "Precio" numeric,
    "FechaAcordada" date,
    "FechaInicio" timestamp with time zone,
    "FechaFin" timestamp with time zone,
    "PagoConfirmado" boolean,
    "EntregaConfirmada" boolean,
    "TransportistaId" bigint,
    CONSTRAINT "Transporte_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Transportista"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "UsuarioId" bigint NOT NULL,
    "Polyline" character varying NOT NULL,
    CONSTRAINT "Transportista_pkey" PRIMARY KEY ("ID")
);

CREATE TABLE IF NOT EXISTS public."Usuario"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "NombreApellido" character varying(50) NOT NULL,
    "Username" character varying(50) NOT NULL,
    "Password" character varying NOT NULL,
    "SuscripcionId" bigint,
    "TipoUsuario" character varying(15) NOT NULL,
    "Email" character varying NOT NULL,
    CONSTRAINT "Usuario_pkey" PRIMARY KEY ("ID"),
    CONSTRAINT "Usuario_Email_key" UNIQUE ("Email"),
    CONSTRAINT "Usuario_Username_key" UNIQUE ("Username")
);

CREATE TABLE IF NOT EXISTS public."Zona"
(
    "ID" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
    "OrganizacionId" bigint NOT NULL,
    "Polyline" character varying NOT NULL,
    CONSTRAINT "Zona_pkey" PRIMARY KEY ("ID")
);

ALTER TABLE IF EXISTS public."Ciudadano"
    ADD CONSTRAINT "Ciudadano_UsuarioId_fkey" FOREIGN KEY ("UsuarioId")
    REFERENCES public."Usuario" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Organizacion"
    ADD CONSTRAINT "FK_Organizacion_Usuario" FOREIGN KEY ("UsuarioId")
    REFERENCES public."Usuario" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."PuntoReciclaje"
    ADD CONSTRAINT "PuntoReciclaje_CiudadanoId_fkey" FOREIGN KEY ("CiudadanoId")
    REFERENCES public."Ciudadano" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    ;


ALTER TABLE IF EXISTS public."PuntoReciclaje_TipoResiduo"
    ADD CONSTRAINT "PuntoReciclaje_TipoResiduo_PuntoReciclajeId_fkey" FOREIGN KEY ("PuntoReciclajeId")
    REFERENCES public."PuntoReciclaje" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."PuntoReciclaje_TipoResiduo"
    ADD CONSTRAINT "PuntoReciclaje_TipoResiduo_TipoResiduoId_fkey" FOREIGN KEY ("TipoResiduoId")
    REFERENCES public."TipoResiduo" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."PuntoResiduo"
    ADD CONSTRAINT "PuntoResiduo_CiudadanoId_fkey" FOREIGN KEY ("CiudadanoId")
    REFERENCES public."Ciudadano" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."RecicladorUrbano"
    ADD CONSTRAINT "RecicladorUrbano_UsuarioId_fkey" FOREIGN KEY ("UsuarioId")
    REFERENCES public."Usuario" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Recorrido"
    ADD CONSTRAINT "Recorrido_RecicladorId_fkey" FOREIGN KEY ("RecicladorId")
    REFERENCES public."RecicladorUrbano" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Recorrido"
    ADD CONSTRAINT "Recorrido_ZonaId_fkey" FOREIGN KEY ("ZonaId")
    REFERENCES public."Zona" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    ;


ALTER TABLE IF EXISTS public."Residuo"
    ADD CONSTRAINT "Residuo_PuntoResiduoId_fkey" FOREIGN KEY ("PuntoResiduoId")
    REFERENCES public."PuntoResiduo" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Residuo"
    ADD CONSTRAINT "Residuo_RecorridoId_fkey" FOREIGN KEY ("RecorridoId")
    REFERENCES public."Recorrido" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Residuo"
    ADD CONSTRAINT "Residuo_TipoResiduoId_fkey" FOREIGN KEY ("TipoResiduoId")
    REFERENCES public."TipoResiduo" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Residuo"
    ADD CONSTRAINT "Residuo_TransaccionId_fkey" FOREIGN KEY ("TransaccionId")
    REFERENCES public."TransaccionResiduo" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Suscripcion"
    ADD CONSTRAINT "FK_Suscripcion_Plan" FOREIGN KEY ("PlanId")
    REFERENCES public."Plan" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."TransaccionResiduo"
    ADD CONSTRAINT "FK_Transaccion_Punto" FOREIGN KEY ("PuntoReciclajeId")
    REFERENCES public."PuntoReciclaje" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."TransaccionResiduo"
    ADD CONSTRAINT "FK_Transaccion_Transporte" FOREIGN KEY ("TransporteId")
    REFERENCES public."Transporte" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Transporte"
    ADD CONSTRAINT "FK_Transporte_Transportista" FOREIGN KEY ("TransportistaId")
    REFERENCES public."Transportista" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Transportista"
    ADD CONSTRAINT "FK_Transportista_Usuario" FOREIGN KEY ("UsuarioId")
    REFERENCES public."Usuario" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public."Usuario"
    ADD CONSTRAINT "FK_Usuario_Suscripcion" FOREIGN KEY ("SuscripcionId")
    REFERENCES public."Suscripcion" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
CREATE INDEX IF NOT EXISTS "fki_FK_Usuario_Suscripcion"
    ON public."Usuario"("SuscripcionId");


ALTER TABLE IF EXISTS public."Zona"
    ADD CONSTRAINT "FK_Zona_Organizacion" FOREIGN KEY ("OrganizacionId")
    REFERENCES public."Organizacion" ("ID")
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
