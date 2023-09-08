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
       ),(
           'Organizacion 1',
           'organizacion1',
           '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
           NULL,
           'ORGANIZACION',
           'organizacion1@email.com'
       );

INSERT INTO public."Ciudadano" ("UsuarioId") VALUES (1),(2),(5);

INSERT INTO public."Organizacion"("RazonSocial", "UsuarioId") VALUES ('Usuario 1 SA', 1),('Usuario 2 SA', 2),('Organizacion SA', 6);

INSERT INTO public."Zona"("OrganizacionId", "Polyline", "Nombre")
VALUES
( 1, '[[-34.634716, -58.558796], [-34.651657, -58.535603], [-34.67316, -58.559628], [-34.663677, -58.568333], [-34.650585, -58.585228], [-34.649334, -58.58329], [-34.643433, -58.583534], [-34.641556, -58.57158], [-34.63838, -58.57357]]', 'Zona 1'),
( 2, '[[-34.667407, -58.582951], [-34.666284, -58.573635],[-34.658771, -58.577580],[-34.665860, -58.591205]]', 'Zona 2'),
( 1, '[[-34.667407, -58.582951], [-34.666284, -58.573635],[-34.658771, -58.577580],[-34.665860, -58.591205]]', 'Zona sin recorridos pendientes')
;

INSERT INTO public."RecicladorUrbano" ("UsuarioId", "OrganizacionId", "ZonaId") VALUES (3, 1, 1),(4, 2, NULL);

INSERT INTO public."Recorrido"("FechaRetiro", "FechaInicio", "FechaFin", "RecicladorId", "ZonaId", "LatitudInicio", "LongitudInicio", "LatitudFin", "LongitudFin")
VALUES
    ('2023-07-03', '2023-07-03 10:00:00-00', '2023-07-03 11:00:00-00', 1, 1, -34.658771, -58.577580, -34.665860, -58.591205),
    ('2023-07-05', NULL, NULL, 1, 1, -34.665860, -58.591205, -34.658771, -58.577580),
    ('2023-08-01', NULL, NULL, 1, 1, -34.665860, -58.591205, -34.658771, -58.577580),
    ('2023-07-03', '2023-07-03 10:00:00-00', '2023-07-03 11:00:00-00', 1, 3, -34.658771, -58.577580, -34.665860, -58.591205)
;

INSERT INTO public."Plan"("Nombre", "Precio", "MesesRenovacion", "CantUsuarios") VALUES ('Free', 0, 0, 0);

INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Plástico');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Papel');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Pilas');
INSERT INTO public."TipoResiduo" ("Nombre") VALUES ('Carton');

INSERT INTO public."Zona_TipoResiduo"("ZonaId", "TipoResiduoId")
VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(2, 4),
(3, 1)
;

INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6519877, -58.5850894, '[1, 1, 0, 1, 1, 0, 0]', 'Prueba 1');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Prueba 2');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (NULL, -34.6519877, -58.5850894, '[1, 1, 0, 1, 1, 0, 0]', 'Punto VERDE 1');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (NULL, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Punto VERDE 2');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (2, -34.6707576, -58.5628052, '[1, 1, 0, 1, 0, 0, 0]', 'Punto de Usuario 2');
INSERT INTO public."PuntoReciclaje" ("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo") VALUES (1, -34.6516877, -58.8850894, '[1, 1, 0, 1, 1, 1, 0]', 'Punto a eliminar');

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
INSERT INTO public."PuntoReciclaje_TipoResiduo" ("PuntoReciclajeId", "TipoResiduoId") VALUES (6, 4);

INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (1, -34.6611203, -58.5422521);
INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (2, -34.66381, -58.581509);
INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (3, -34.6611203, -58.5422521);
INSERT INTO public."PuntoResiduo" ("CiudadanoId", "Latitud", "Longitud") VALUES (3, -34.6611203, -58.5422521);

INSERT INTO public."PuntoResiduo_Zona" ("PuntoResiduoId", "ZonaId")
VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 2),
(3, 3)
;

INSERT INTO public."Transportista"("UsuarioId", "Polyline") VALUES (1, '[[-34.667407, -58.582951], [-34.666284, -58.573635],[-34.658771, -58.577580],[-34.665860, -58.591205]]');
INSERT INTO public."Transportista"("UsuarioId", "Polyline") VALUES (2, '[[-34.667407, -58.582951], [-34.666284, -58.573635],[-34.658771, -58.577580],[-34.665860, -58.591205]]');

<<<<<<< HEAD
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId", "PrecioSugerido") VALUES (40, NULL, '2020-01-01 08:00:00+00', '2020-01-02 08:00:00+00', false, false, 1, 2500);
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId", "PrecioSugerido") VALUES (50, NULL, '2020-02-01 08:00:00+00', '2020-02-02 08:00:00+00', true, true, 2, 0);
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId", "PrecioSugerido") VALUES (60, '2020-03-02', NULL, NULL, true, false, NULL, 1500);
=======
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId") VALUES (40, NULL, '2020-01-01 08:00:00+00', '2020-01-02 08:00:00+00', false, false, 1);
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId") VALUES (50, NULL, '2020-02-01 08:00:00+00', '2020-02-02 08:00:00+00', true, true, 2);
INSERT INTO public."Transporte"("Precio", "FechaAcordada", "FechaInicio", "FechaFin", "PagoConfirmado", "EntregaConfirmada", "TransportistaId") VALUES (50, NULL, '2020-02-01 08:00:00+00', '2020-02-02 08:00:00+00', true, true, null);
>>>>>>> develop


INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-01-01 08:00:00+00', '2023-01-02 08:00:00+00', 1, 1);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-02-01 08:00:00+00', '2023-02-02 08:00:00+00', 2, 2);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-02-01 08:00:00+00', '2023-02-02 08:00:00+00', 1, 2);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-03-01 08:00:00+00', '2023-03-02 08:00:00+00', 1, null);
INSERT INTO public."TransaccionResiduo"("FechaPrimerContacto", "FechaEfectiva", "PuntoReciclajeId", "TransporteId") VALUES ('2023-03-01 08:00:00+00', '2023-03-02 08:00:00+00', 1, 3);

INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, NULL, NULL, 'Prueba 1', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, NULL, NULL, 'Residuo con limite', '2023-07-10 19:41:00-00');
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', '2023-07-22 19:41:00-00', 1, 1, NULL, NULL, 'Residuo Fulfilled', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 4, NULL, NULL, 'Carton', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:00:00-00', NULL, 1, 1, NULL, 3, 'Residuo en recorrido', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:00:00-00', NULL, 2, 1, NULL, 3, 'Residuo en recorrido 2', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-01-01 20:00:00-00', NULL, 2, 4, 1, NULL, 'Residuo en transaccion 1', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-01-01 20:00:00-00', NULL, 2, 4, 1, NULL, 'Residuo en transaccion 1-2', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-01-01 20:00:00-00', NULL, 4, 1, NULL, NULL, 'Residuo sin Zona', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 2, 4, NULL, NULL, 'Carton que no acepta ninguna zona', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 3, 4, NULL, NULL, 'Residuo con zona sin recorridos', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, NULL, NULL, 'Residuo para recorrido', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', '2023-07-22 19:41:00-00', 1, 1, NULL, 3, 'Residuo Fulfilled en Recorrido', NULL);
INSERT INTO public."Residuo" ("FechaCreacion", "FechaRetiro", "PuntoResiduoId", "TipoResiduoId", "TransaccionId", "RecorridoId", "Descripcion", "FechaLimiteRetiro") VALUES ('2023-07-02 19:41:00-00', NULL, 1, 1, 4, NULL, 'Residuo en transaccion 4', NULL);

INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId", "PuntoReciclajeId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'PENDIENTE', 2, 1, 1, 5);
INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId", "PuntoReciclajeId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'PENDIENTE', 1, 2, 2, 5);
INSERT INTO public."Solicitud"("FechaCreacion", "FechaModificacion", "Estado", "CiudadanoSolicitanteId", "CiudadanoSolicitadoId", "ResiduoId", "PuntoReciclajeId")	VALUES ('2023-07-02 19:41:00-00', '2023-07-02 19:41:00-00', 'CANCELADA', 1, 2, 12, 5);
