INSERT INTO public."Usuario"( "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email" )
	VALUES (
        'Usuario Existente',
        'existing',
        '$2a$10$vQ35vpn5y4RPXXNm4blxWer6NVn0Pl3GHmSn5XRr2VdGITgpn1j0G', -- 1234
        NULL,
        'CIUDADANO',
        'existing@email.com'
    );
