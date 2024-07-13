package com.ArcaneScrolls.controller;


import com.ArcaneScrolls.model.Note;
import com.ArcaneScrolls.model.User;
import com.ArcaneScrolls.service.NoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class FeaturesController {

    private final NoteService noteService;

    @Autowired
    public FeaturesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/noteInfo/{id}")
    public String getNoteInfo(@PathVariable Long id, Model model, HttpSession session) throws Exception {

        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }

        Optional<Note> noteOptional = Optional.ofNullable(noteService.getNoteById(id, session));

        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            model.addAttribute("note", note);
            return "info-note";
        } else {
            return "redirect:/notes";
        }
    }

    @GetMapping("/viewNote/{id}")
    public String viewNote(@PathVariable Long id, Model model, HttpSession session) throws Exception {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }
        Optional<Note> noteOptional = Optional.ofNullable(noteService.getNoteById(id, session));
        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            model.addAttribute("note", note);
            return "view-note";
        }
        return "notes";
    }

    @GetMapping("/search")
    public String searchNotes(@RequestParam("keyword") String keyword, Model model, HttpSession session) throws Exception {

        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }

        List<Note> notes = noteService.searchNotes(authenticatedUser, keyword, session);

        model.addAttribute("notes", notes);
        model.addAttribute("username", authenticatedUser.getUsername());

        return "notes";
    }

}
