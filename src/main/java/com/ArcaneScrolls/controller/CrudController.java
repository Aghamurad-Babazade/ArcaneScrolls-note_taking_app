package com.ArcaneScrolls.controller;

import com.ArcaneScrolls.model.Note;
import com.ArcaneScrolls.model.User;
import com.ArcaneScrolls.service.NoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class CrudController {

    private final NoteService noteService;

    public CrudController(NoteService noteService) {
        this.noteService = noteService;
    }

    private boolean isUserAuthenticated(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/notes")
    public String showNotesPage(Model model, HttpSession session) throws Exception {

        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }

        List<Note> notes = noteService.getAllNotes(authenticatedUser, session);

        model.addAttribute("notes", notes);
        model.addAttribute("username", authenticatedUser.getUsername());

        return "notes";
    }

    @GetMapping("/addNote")
    public String showAddNoteForm(HttpSession session, Model model) {
        if (isUserAuthenticated(session)) {
            model.addAttribute("theNote", new Note());
            return "add-note";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/addNote")
    public String addNote(@ModelAttribute("theNote") Note theNote, HttpSession session) throws Exception {
        if (isUserAuthenticated(session)) {
            User authenticatedUser = (User) session.getAttribute("user");
            noteService.saveNoteForUser(authenticatedUser, theNote, session);
            return "redirect:/notes";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editNote/{id}")
    public String editNoteForm(@PathVariable Long id, Model model, HttpSession session) throws Exception {

        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }

        Note note = noteService.getNoteById(id, session);
        model.addAttribute("theNote", note);
        return "edit-note";
    }

    @PostMapping("/editNote")
    public String editNote(@ModelAttribute("theNote") Note theNote, HttpSession session) throws Exception {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }
        theNote.setUser(authenticatedUser);
        noteService.updateNote(theNote, session);
        return "redirect:/notes";
    }

    @GetMapping("/deleteNote/{id}")
    public String deleteNote(@PathVariable Long id, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser == null) {
            return "redirect:/login";
        }
        noteService.deleteNote(id);
        return "redirect:/notes";
    }

}
