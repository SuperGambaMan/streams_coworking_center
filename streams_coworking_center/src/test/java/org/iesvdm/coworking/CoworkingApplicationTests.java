package org.iesvdm.coworking;

import org.iesvdm.coworking.modelo.Miembro;
import org.iesvdm.coworking.modelo.Reserva;
import org.iesvdm.coworking.modelo.Sala;
import org.iesvdm.coworking.repositorio.MiembroRepository;
import org.iesvdm.coworking.repositorio.ReservaRepository;
import org.iesvdm.coworking.repositorio.SalaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

@SpringBootTest
class CoworkingApplicationTests {

    @Autowired
    MiembroRepository miembroRepository;

    @Autowired
    ReservaRepository reservaRepository;

    @Autowired
    SalaRepository salaRepository;

    @Test
    void testMiembros() {

        miembroRepository.findAll().forEach(System.out::println);

    }

    @Test
    void testReservas() {

        reservaRepository.findAll().forEach(System.out::println);

    }

    @Test
    void testSalas() {

        salaRepository.findAll().forEach(System.out::println);

    }

    //1. Devuelve un listado de todas las reservas realizadas durante el año 2025, cuya sala tenga un precio_hora superior a 25€.
    @Test
    void test1(){
        List<Reserva> reservas =  reservaRepository.findAll();

        var reservas2025 = reservas.stream()
                .filter(r->r.getFecha().isAfter(LocalDate.of(2024,12,31))
                        && r.getFecha().isBefore(LocalDate.of(2026,01,01)))
                .filter(r->r.getSala().getPrecioHora().compareTo(BigDecimal.valueOf(25))>0)
                .map(r->r.getId())
                .toList();

        reservas2025.forEach(System.out::println);
        Assertions.assertEquals(6,reservas2025.size());
    }

    // 2. Devuelve un listado de todos los miembros que NO han realizado ninguna reserva.
    @Test
    void test2(){


        List<Miembro> miembros = miembroRepository.findAll();

        var miembrosSinReserva = miembros.stream()
                .filter(m->m.getReservas().size()==0)
                .map(m->m.getNombre())
                .toList();

        miembrosSinReserva.forEach(System.out::println);
    }
    // 3. Devuelve una lista de los id's, nombres y emails de los miembros que no tienen el teléfono registrado.
    @Test
            void test3(){
        List<Miembro> miembros = miembroRepository.findAll();

        var miembroSinTlf = miembros.stream()
                .filter(m->m.getTelefono()==null)
                .sorted(comparing((Miembro m)->m.getNombre(), reverseOrder()))
                .map(m->"ID: "+m.getId()+" Nombre: "+m.getNombre()+" Email: "+m.getEmail())
                .toList();

        miembroSinTlf.forEach(System.out::println);

        Assertions.assertTrue(miembroSinTlf.contains("ID: 18 Nombre: Óscar Núñez Email: oscar.nunez@ejemplo.com"));

    }

    // El listado tiene que estar ordenado inverso alfabéticamente por nombre (z..a).
    // 4. Devuelve un listado con los id's y emails de los miembros que se hayan registrado con una cuenta de yahoo.es
    // en el año 2024.

    @Test
    void test4(){
        List<Miembro> miembros = miembroRepository.findAll();
        var miembroYahoo = miembros.stream()
                .filter(m->m.getEmail().contains("yahoo.es") && m.getFechaAlta().getYear()==2024)
                .map(m->"ID: "+m.getId()+" Email: "+m.getEmail())
                .toList();

        miembroYahoo.forEach(System.out::println);

    }
    // 5. Devuelve un listado de los miembros cuyo primer apellido es Martín. El listado tiene que estar ordenado

    @Test
    void test5(){
        List<Miembro> miembros = miembroRepository.findAll();

        var miembroMartin = miembros.stream()
                .filter(m->m.getNombre().contains(" Martín"))
                .sorted(comparing((Miembro m)->m.getFechaAlta(),reverseOrder()).thenComparing((Miembro m)->m.getNombre()))
                .map(m->m.getNombre())
                .toList();

        miembroMartin.forEach(System.out::println);

        Assertions.assertTrue(miembroMartin.contains("Paula Martín"));
    }
    // por fecha de alta en el coworking de más reciente a menos reciente y nombre y apellidos en orden alfabético.
    // 6. Devuelve el gasto total (estimado) que ha realizado la miembro Ana Beltrán en reservas del coworking.
    @Test
            void test6(){
        List<Reserva> reservas = reservaRepository.findAll();


         var miembroGasto = reservas.stream()
                .filter(r->r.getMiembro().getNombre().equalsIgnoreCase("Ana Beltrán"))
                .mapToDouble(m-> (m.getSala().getPrecioHora().doubleValue()) * (m.getHoras().doubleValue()))
                 .sum();


        System.out.println("Total: "+miembroGasto);

        Assertions.assertEquals(124.0,miembroGasto);
    }


    // 7. Devuelve el listado de las 3 salas de menor precio_hora.
    @Test
    void test7() {
        List<Sala> salas = salaRepository.findAll();
        var precioMin = salas.stream()
                .sorted(comparing((Sala s)->s.getPrecioHora()))
                .map(s->s.getNombre())
                .limit(3)
                .toList();

        precioMin.forEach(System.out::println);
        Assertions.assertTrue(precioMin.contains("Box Cibeles"));
    }


    // 8. Devuelve la reserva a la que se le ha aplicado la mayor cuantía de descuento sobre el precio sin descuento
    // (precio_hora × horas).
    @Test
    void test8(){
        List<Reserva> reservas = reservaRepository.findAll();

        var resul = reservas.stream()
                .mapToDouble(r->r.getSala().getPrecioHora().doubleValue()*r.getHoras().doubleValue())
                .max();

        System.out.println(resul);

        Assertions.assertEquals(135,resul.getAsDouble());

    }

    // 9. Devuelve los miembros que hayan tenido alguna reserva con estado 'ASISTIDA' y exactamente 10 asistentes.
    @Test
    void test9(){
        List<Miembro> miembros = miembroRepository.findAll();

        var mAsistido = miembros.stream()
                .flatMap(m->m.getReservas().stream()
                        .filter(r->r.getEstado().equalsIgnoreCase("ASISTIDA")&& r.getAsistentes()==10))
                .map(m->m.getMiembro().getNombre())
                .toList();

        mAsistido.forEach(System.out::println);

        Assertions.assertTrue(mAsistido.contains("Óscar Núñez"));
    }

    // 10. Devuelve el valor mínimo de horas reservadas (campo calculado 'horas') en una reserva.

    @Test
    void test10(){
        List<Reserva> reservas = reservaRepository.findAll();
        var reser = reservas.stream()
                .mapToDouble(r->r.getHoras().doubleValue())
                .min();

        System.out.println(reser);
    }
    // 11. Devuelve un listado de las salas que empiecen por 'Sala' y terminen por 'o',
    // y también las salas que terminen por 'x'.

    @Test
    void test11(){
        List<Sala> salas = salaRepository.findAll();

        var sal = salas.stream()
                .filter(s->s.getNombre().startsWith("Sala") && s.getNombre().endsWith("o") || s.getNombre().endsWith("x"))
                .map(s->s.getNombre())
                .toList();

        sal.forEach(System.out::println);
        Assertions.assertTrue(sal.contains("Sala Prado"));
    }
    // 12. Devuelve un listado que muestre todas las reservas y salas en las que se ha registrado cada miembro.
    // El resultado debe mostrar todos los datos del miembro primero junto con un sublistado de sus reservas y salas.
    @Test
    void test12(){


        List<Reserva> reservas = reservaRepository.findAll();
        var listado = reservas.stream()
                .sorted(comparing((Reserva r)->r.getMiembro().getNombre()))
                .map(r->{
                    String miembro = r.getMiembro().getNombre();
                    Long re = r.getId();
                    String salas = r.getSala().getNombre();



                    return  miembro+"\n"+salas+" ID reserva: "+re;
                })
                .toList();

        listado.forEach(System.out::println);
        Assertions.assertTrue(listado.contains("Ana Beltrán\n" +
                "Sala Retiro ID reserva: 1"));
    }




    // El listado debe mostrar los datos de los miembros ordenados alfabéticamente por nombre.
    // 13. Devuelve el total de personas que podrían alojarse simultáneamente en el centro en base al aforo de todas las salas.
    @Test
    void test13(){
        List<Sala> salas = salaRepository.findAll();

        var salaTotal = salas.stream()
                .mapToInt(s->s.getAforo())
                .sum();

        System.out.println(salaTotal);

        Assertions.assertEquals(266, salaTotal);
    }
    // 14. Calcula el número total de miembros (diferentes) que tienen alguna reserva.

    @Test
    void test14(){
        List<Miembro> miembros = miembroRepository.findAll();

        var miembroTota = miembros.stream()
                .filter(m->m.getReservas().size()>0)
                .distinct()
                .count();

        System.out.println(miembroTota);
        Assertions.assertEquals(20,miembroTota);

    }
    // 15. Devuelve el listado de las salas para las que se aplica un descuento porcentual (descuento_pct) superior al 10%
    // en alguna de sus reservas.

    @Test
    void test15(){
        List<Reserva>reservas = reservaRepository.findAll();

        var salaDes = reservas.stream()
                .filter(r->r.getDescuentoPct()!=null && r.getDescuentoPct().compareTo(BigDecimal.valueOf(10))>0)
                .map(r->r.getSala().getNombre())
                .toList();

        salaDes.forEach(System.out::println);

        Assertions.assertTrue(salaDes.contains("Sala Atocha"));


    }
    // 16. Devuelve el nombre del miembro que pagó la reserva de mayor cuantía (precio_hora × horas aplicando el descuento).
    // 17. Devuelve los nombres de los miembros que hayan coincidido en alguna reserva con la miembro Ana Beltrán
    // (misma sala y fecha con solape horario).
    // 18. Devuelve el total de lo ingresado por el coworking en reservas para el mes de enero de 2025.
    // 19. Devuelve el conteo de cuántos miembros tienen la observación 'Requiere equipamiento especial' en alguna de sus reservas.
    @Test
    void test19(){
        List<Reserva>reservas = reservaRepository.findAll();
        var requiere = reservas.stream()
                .filter(r-> r.getObservaciones()!=null && r.getObservaciones().equalsIgnoreCase("Requiere equipamiento especial"))
                .map(r->r.getMiembro().getNombre())
                .toList();

        requiere.forEach(System.out::println);

    }
    // 20. Devuelve cuánto se ingresaría por la sala 'Auditorio Sol' si estuviera reservada durante todo su horario de apertura
    // en un día completo (sin descuentos).

    @Test
    void test20(){
        List<Sala> salas = salaRepository.findAll();

        var cuenta = salas.stream()
                .filter(s->s.getNombre().equalsIgnoreCase("Auditorio Sol"))
                .mapToDouble(s->s.getPrecioHora().doubleValue()*12)
                .sum();

        System.out.println(cuenta);

        Assertions.assertEquals(540.0,cuenta);



    }
}
