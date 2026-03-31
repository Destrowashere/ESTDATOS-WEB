package com.biblioteca.web;

import com.biblioteca.servicio.BibliotecaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LibroController {

    private final BibliotecaServicio bibliotecaServicio;

    public LibroController(BibliotecaServicio bibliotecaServicio) {
        this.bibliotecaServicio = bibliotecaServicio;
    }

    @GetMapping("/libros")
    public String libros(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("titulo", "Libros — Biblioteca");
        model.addAttribute("libros", bibliotecaServicio.listarLibrosFiltrados(q));
        model.addAttribute("q", q != null ? q : "");
        return "libros";
    }

    @PostMapping("/libros")
    public String agregarLibro(
            @RequestParam String titulo,
            @RequestParam String autor,
            RedirectAttributes redirectAttributes) {
        String err = bibliotecaServicio.agregarLibro(titulo, autor);
        if (err != null) {
            redirectAttributes.addFlashAttribute("error", err);
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Libro registrado correctamente.");
        }
        return "redirect:/libros";
    }
}
