INSERT INTO public."Usuario"( "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email" )
	VALUES (
        'Usuario Existente',
        'existing',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'CIUDADANO',
        'existing@email.com'
    ),
	(
        'Usuario 2',
        'username2',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'CIUDADANO',
        'existing2@email.com'
    );

INSERT INTO public."Ciudadano" ("UsuarioId") VALUES (1),(2);


INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Plástico');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Papel');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Pilas');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Carton');

INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6519877, -58.5850894, '[1, 1, 0, 1, 1, 0, 0]', 'Prueba 1');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Prueba 2');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (NULL, -34.6519877, -58.5850894, '[1, 1, 0, 1, 1, 0, 0]', 'Punto VERDE 1');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (NULL, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Punto VERDE 2');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (2, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Punto de Usuario 2');

INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (1, 1);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (1, 2);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (1, 4);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (2, 3);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (2, 4);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (3, 1);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (3, 2);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (3, 4);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (4, 3);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (4, 4);
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (5, 4);

INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (1, -34.6611203, -58.5422521);
INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (2, -35.6611203, -58.5422521);

INSERT INTO public."Transportista"("UsuarioId", "Polyline") VALUES (1, 'fakePolyline');
INSERT INTO public."Transportista"("UsuarioId", "Polyline") VALUES (2, 'fakePolyline');

INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId") VALUES (40, NULL, '2020-01-01 08:00:00+00', '2020-01-02 08:00:00+00', false, false, 1);
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId") VALUES (50, NULL, '2020-02-01 08:00:00+00', '2020-02-02 08:00:00+00', true, true, 2);

INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-01-01 08:00:00+00', '2023-01-02 08:00:00+00', 1, 1);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-02-01 08:00:00+00', '2023-02-02 08:00:00+00', 2, 2);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-02-01 08:00:00+00', '2023-02-02 08:00:00+00', 1, 2);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-03-01 08:00:00+00', '2023-03-02 08:00:00+00', 1, null);

INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, 1, NULL, 'Prueba 1', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, 2, NULL, 'Residuo con limite', '2023-07-10 19:41:00-00');
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', '2023-07-22 19:41:00-00', 1, 1, 3, NULL, 'Residuo Fulfilled', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 4, 1, NULL, 'Carton', NULL);

INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'PENDIENTE', 2, 1, 1);
INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'PENDIENTE', 1, 2, 2);
