package com.zmc.ierbar_web_app.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.zmc.ierbar_web_app.models.factory.CategoriePlanta;
import com.zmc.ierbar_web_app.models.factory.PlantaFactory;
import com.zmc.ierbar_web_app.models.simple_factory.Arbore;
import com.zmc.ierbar_web_app.models.simple_factory.Arbust;
import com.zmc.ierbar_web_app.models.simple_factory.Floare;
import com.zmc.ierbar_web_app.models.simple_factory.Ierburi;
import com.zmc.ierbar_web_app.models.simple_factory.Planta;
import com.zmc.ierbar_web_app.models.simple_factory.TipPlanta;

public class PlantaFactoryTest {

    @Test
    public void testCreazaFloare(){
        PlantaFactory factory = new PlantaFactory();
        Planta trandafir = factory.creazaPlanta(
                CategoriePlanta.FLOARE, 1, "trandafir", "Rosa", "Rosaceae", "O floare parfumata",
                0.5f, "Primavara", null, TipPlanta.ORNAMENTALA, "Gradina", "http://img.jpg",
                10, "rosu", null, null, false, false, null, false
        );

        Assertions.assertNotNull(trandafir, "Planta creata nu trebuie sa fie null");
        Assertions.assertTrue(trandafir instanceof Floare, "Planta creata trebuie sa fie o instanta a clasei Floare");
        Assertions.assertEquals(CategoriePlanta.FLOARE, trandafir.getCategorie_planta(), "Categoria plantei trebuie sa fie FLOARE");
    }

    @Test
    public void testCreazaArbore(){
        PlantaFactory factory = new PlantaFactory();
        Planta stejar = factory.creazaPlanta(
                CategoriePlanta.ARBORE, 2, "stejar", "Quercus", "Fagaceae", "Un copac mare",
                20.0f, "Primavara", null, TipPlanta.ORNAMENTALA, "Padure", "http://img.jpg",
                0, null, "Rotunda", "Lobata", true, false, null, false
        );

        Assertions.assertNotNull(stejar, "Planta creata nu trebuie sa fie null");
        Assertions.assertTrue(stejar instanceof Arbore, "Planta creata trebuie sa fie o instanta a clasei Arbore");
        Assertions.assertEquals(CategoriePlanta.ARBORE, stejar.getCategorie_planta(), "Categoria plantei trebuie sa fie ARBORE");
    }

    @Test
    public void testCreazaArbust(){
        PlantaFactory factory = new PlantaFactory();
        Planta maces = factory.creazaPlanta(
                CategoriePlanta.ARBUST, 3, "maces", "Rosa canina", "Rosaceae", "Un arbust cu fructe",
                1.5f, "Primavara", null, TipPlanta.COMESTIBILA, "Gradina", "http://img.jpg",
                0, null, null, null, false, true, null, false
        );

        Assertions.assertNotNull(maces, "Planta creata nu trebuie sa fie null");
        Assertions.assertTrue(maces instanceof Arbust, "Planta creata trebuie sa fie o instanta a clasei Arbust");
        Assertions.assertEquals(CategoriePlanta.ARBUST, maces.getCategorie_planta(), "Categoria plantei trebuie sa fie ARBUST");
    }

    @Test
    public void testCreazaIerburi(){
        PlantaFactory factory = new PlantaFactory();
        Planta busuioc = factory.creazaPlanta(
                CategoriePlanta.IERBURI, 4, "busuioc", "Ocimum basilicum", "Lamiaceae", "O planta aromatica",
                0.3f, "Vara", null, TipPlanta.AROMATICA, "Gradina", "http://img.jpg",
                0, null, null, null, false, false, "Tulpina moale", true
        );

        Assertions.assertNotNull(busuioc, "Planta creata nu trebuie sa fie null");
        Assertions.assertTrue(busuioc instanceof Ierburi, "Planta creata trebuie sa fie o instanta a clasei Ierburi");
        Assertions.assertEquals(CategoriePlanta.IERBURI, busuioc.getCategorie_planta(), "Categoria plantei trebuie sa fie IERBURI");
    }

    @Test
    public void testCreazaPlantaInvalida(){
        PlantaFactory factory = new PlantaFactory();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            factory.creazaPlanta(
                    null, 5, "planta_invalida", "N/A", "N/A", "N/A",
                    0.0f, "N/A", null, TipPlanta.ORNAMENTALA, "N/A", "N/A",
                    0, null, null, null, false, false, null, false
            );
        }, "Trebuie sa arunce o exceptie pentru categoria de planta invalida");
    }
}