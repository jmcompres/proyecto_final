package com.backend;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GestorArchivos {

    private static String dirFichero = "ficheros/todo.dat";

    public static void guardarData()
    {
        GestorRutas data = GestorRutas.getInstance();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dirFichero))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GestorRutas cargarData()
    {
        GestorRutas neoData = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dirFichero))) {
            neoData = (GestorRutas) ois.readObject();
        } catch(EOFException e) {
            System.out.println("Creando nuevo gestor de rutas y paradas");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return neoData;
    }
}
