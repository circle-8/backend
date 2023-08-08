INSERT INTO public."Usuario"("NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email")
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
    ),
    (
        'Reciclador 1',
        'reciclador1',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'RECICLADOR_URBANO',
        'reciclador1@email.com'
    ),
     (
        'Reciclador 2',
        'reciclador2',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'RECICLADOR_URBANO',
        'reciclador2@email.com'
    ),(
        'Usuario 3',
        'username3',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'CIUDADANO',
        'existing3@email.com'
    );

INSERT INTO public."Ciudadano" ("UsuarioId") VALUES (1),(2),(5);

INSERT INTO public."Organizacion"("RazonSocial", "UsuarioId") VALUES ('Usuario 1 SA', 1),('Usuario 2 SA', 2);

INSERT INTO public."Zona"("OrganizacionId", "Polyline", "Nombre")
	VALUES (
		1,
		'[[-34.634716, -58.558796], [-34.651657, -58.535603], [-34.67316, -58.559628], [-34.663677, -58.568333], [-34.650585, -58.585228], [-34.649334, -58.58329], [-34.643433, -58.583534], [-34.641556, -58.57158], [-34.63838, -58.57357]]',
		'Zona 1'),(
		2,
		'[[-34.667407, -58.582951], [-34.666284, -58.573635],[-34.658771, -58.577580],[-34.665860, -58.591205]]',
		'Zona 2');

INSERT INTO public."RecicladorUrbano" ("UsuarioId", "OrganizacionId", "ZonaId") VALUES (3, 1, 1),(4, 2, NULL);

INSERT INTO public."Recorrido"("FechaRetiro", "FechaInicio", "FechaFin", "RecicladorId", "ZonaId")
	VALUES ('2023-07-03', '2023-07-03 10:00:00-00', '2023-07-03 11:00:00-00', 1, 1), ('2023-07-05', NULL, NULL, 1, 1);

INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Plástico');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Papel');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Pilas');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Carton');

INSERT INTO public."Zona_TipoResiduo"("ZonaId", "TipoResiduoId") VALUES (1, 1),(1, 2),(2, 2),(2, 3),(2, 4);

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
INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (2, -34.66381, -58.581509);
INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (3, -34.6611203, -58.5422521);

INSERT INTO public."PuntoResiduo_Zona" ("PuntoResiduoId", "ZonaId")	VALUES (1, 1),(1, 2),(2, 1);

INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, NULL, NULL, 'Prueba 1', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, NULL, NULL, 'Residuo con limite', '2023-07-10 19:41:00-00');
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', '2023-07-22 19:41:00-00', 1, 1, NULL, NULL, 'Residuo Fulfilled', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 4, NULL, NULL, 'Carton', NULL);

INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId", "PuntoReciclajeId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'PENDIENTE', 2, 1, 1, 5);
INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId", "PuntoReciclajeId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'PENDIENTE', 1, 2, 2, 5);
