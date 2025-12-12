-- 1. Devuelve un listado de todas las reservas realizadas durante el año 2025, cuya sala tenga un precio_hora superior a 25€.
    select * from reserva r join sala s on r.sala_id=s.sala_id where r.fecha>='2025-01-01' and r.fecha<='2025-12-31' and s.precio_hora>25;
-- 2. Devuelve un listado de todos los miembros que NO han realizado ninguna reserva.

    select * from miembro m left join reserva r on r.miembro_id=m.miembro_id where r.miembro_id is null;
-- 3. Devuelve una lista de los id's, nombres y emails de los miembros que no tienen el teléfono registrado.
-- El listado tiene que estar ordenado inverso alfabéticamente por nombre (z..a).

    select m.miembro_id, m.nombre, m.email from miembro m where m.telefono is null order by m.nombre asc ;
-- 4. Devuelve un listado con los id's y emails de los miembros que se hayan registrado con una cuenta de yahoo.es
-- en el año 2024.
    select m.miembro_id , m.email from miembro m where m.email  like'%yahoo.es' and m.fecha_alta>='2024-01-01' and m.fecha_alta<='2024-12-31';
-- 5. Devuelve un listado de los miembros cuyo primer apellido es Martín. El listado tiene que estar ordenado
-- por fecha de alta en el coworking de más reciente a menos reciente y nombre y apellidos en orden alfabético.

    select * from miembro m where m.nombre like '%Pepe' order by m.fecha_alta asc, m.nombre asc ;


-- 7. Devuelve el listado de las 3 salas de menor precio_hora.

    select * from sala s order by s.precio_hora asc limit 3;
-- 9. Devuelve los miembros que hayan tenido alguna reserva con estado 'ASISTIDA' y exactamente 10 asistentes.
    select * from miembro m join reserva r on r.miembro_id=m.miembro_id where r.estado like 'ASISTIDA' and r.asistentes=10;
-- 10. Devuelve el valor mínimo de horas reservadas (campo calculado 'horas') en una reserva.
    select * from reserva r order by r.horas asc limit 1;
-- 11. Devuelve un listado de las salas que empiecen por 'Sala' y terminen por 'o',
-- y también las salas que terminen por 'x'.
    select * from sala s where s.nombre like 'Sala%' and '%o' and'%x';

-- 13. Devuelve el total de personas que podrían alojarse simultáneamente en el centro en base al aforo de todas las salas.
    select SUM(s.aforo) as aforo_total from sala s;
-- 14. Calcula el número total de miembros (diferentes) que tienen alguna reserva.
