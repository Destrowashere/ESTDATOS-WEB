package com.biblioteca.servicio;

import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BibliotecaServicio {

    private final List<Libro> libros = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();

    @PostConstruct
    public void iniciarDatos() {
        registrarLibroInterno("Cien años de soledad", "García Márquez");
        registrarLibroInterno("Don Quijote", "Cervantes");
        registrarLibroInterno("El Principito", "Saint-Exupéry");
        registrarLibroInterno("Libro de la vida", "Santa Teresa de Jesús");

        Usuario ana = registrarUsuarioInterno("Ana Martínez", "U001");
        Usuario luis = registrarUsuarioInterno("Luis Gómez", "U002");
        registrarUsuarioInterno("María Ruiz", "U003");

        Libro dq = buscarLibroPorTitulo("Don Quijote");
        Libro lv = buscarLibroPorTitulo("Libro de la vida");
        if (ana != null && dq != null) {
            prestarInterno(dq, ana);
        }
        if (luis != null && lv != null) {
            prestarInterno(lv, luis);
        }
    }

    private void registrarLibroInterno(String titulo, String autor) {
        if (buscarLibroPorTitulo(titulo) != null) {
            return;
        }
        libros.add(new Libro(titulo.trim(), autor.trim()));
    }

    private Usuario registrarUsuarioInterno(String nombre, String id) {
        if (buscarUsuarioPorId(id) != null) {
            return buscarUsuarioPorId(id);
        }
        Usuario u = new Usuario(nombre.trim(), id.trim());
        usuarios.add(u);
        return u;
    }

    private void prestarInterno(Libro libro, Usuario usuario) {
        if (!libro.isDisponible() || !usuario.puedeTomarOtroLibro()) {
            return;
        }
        libro.prestarA(usuario);
        usuario.registrarPrestamo(libro);
    }

    public List<Libro> listarLibrosFiltrados(String consulta) {
        if (consulta == null || consulta.isBlank()) {
            return new ArrayList<>(libros);
        }
        String q = consulta.toLowerCase(Locale.ROOT).trim();
        return libros.stream()
                .filter(l -> l.getTitulo().toLowerCase(Locale.ROOT).contains(q)
                        || l.getAutor().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        if (titulo == null) {
            return null;
        }
        String t = titulo.trim();
        for (Libro l : libros) {
            if (l.getTitulo().equalsIgnoreCase(t)) {
                return l;
            }
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(String id) {
        if (id == null) {
            return null;
        }
        String clave = id.trim();
        for (Usuario u : usuarios) {
            if (u.getId().equalsIgnoreCase(clave)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public String agregarLibro(String titulo, String autor) {
        String t = titulo != null ? titulo.trim() : "";
        String a = autor != null ? autor.trim() : "";
        if (t.isEmpty() || a.isEmpty()) {
            return "Título y autor son obligatorios.";
        }
        if (buscarLibroPorTitulo(t) != null) {
            return "Ya existe un libro con ese título.";
        }
        libros.add(new Libro(t, a));
        return null;
    }

    public String agregarUsuario(String nombre, String id) {
        String n = nombre != null ? nombre.trim() : "";
        String i = id != null ? id.trim() : "";
        if (n.isEmpty() || i.isEmpty()) {
            return "Nombre e identificador son obligatorios.";
        }
        if (buscarUsuarioPorId(i) != null) {
            return "Ya existe un usuario con ese identificador.";
        }
        usuarios.add(new Usuario(n, i));
        return null;
    }

    /**
     * Elimina el usuario: libera sus préstamos (libros pasan a disponibles) y lo quita del catálogo.
     */
    public String eliminarUsuario(String id) {
        Usuario u = buscarUsuarioPorId(id);
        if (u == null) {
            return "Usuario no encontrado.";
        }
        List<Libro> prestados = new ArrayList<>();
        for (Libro l : libros) {
            if (l.getPrestadoA() == u) {
                prestados.add(l);
            }
        }
        for (Libro l : prestados) {
            u.liberarPrestamo(l);
            l.devolver();
        }
        usuarios.remove(u);
        return null;
    }
}
