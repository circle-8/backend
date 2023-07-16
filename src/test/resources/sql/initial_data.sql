INSERT INTO public."Usuario"( "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email" )
	VALUES (
        'Usuario Existente',
        'existing',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'CIUDADANO',
        'existing@email.com'
    );

INSERT INTO public."Ciudadano" ("UsuarioId") VALUES (1);

INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Plástico');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Papel');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Pilas');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Carton');

INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6519877, -58.5850894, '[1, 1, 0, 1, 1, 0, 0]', 'Prueba 1');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Prueba 2');

INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (1, 1);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (1, 2);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (1, 4);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (2, 3);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (2, 4);

INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (1, -34.6611203, -58.5422521);

INSERT INTO public."Residuo" ("FechaCreacion", "FechaLimiteRetiro", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId") VALUES ('2023-07-02 19:41:00+00', NULL, NULL, 1, 1, NULL, NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaLimiteRetiro", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId") VALUES ('2023-07-02 19:41:00+00', '2023-07-10 19:41:00+00', NULL, 1, 1, NULL, NULL);
