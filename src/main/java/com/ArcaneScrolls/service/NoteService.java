package com.ArcaneScrolls.service;

import com.ArcaneScrolls.config.EncryptionUtil;
import com.ArcaneScrolls.model.Note;
import com.ArcaneScrolls.model.User;
import com.ArcaneScrolls.repository.NoteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes(User user, HttpSession session) throws Exception {
        List<Note> notes = noteRepository.findByUserUsername(user.getUsername());
        for (Note note : notes) {
            note.setHeadline(EncryptionUtil.decrypt(note.getHeadline(), (String) session.getAttribute("password")));
            note.setNote(EncryptionUtil.decrypt(note.getNote(), (String) session.getAttribute("password")));
        }
        return notes;
    }

    public void saveNoteForUser(User user, Note note, HttpSession session) throws Exception {
        note.setHeadline(EncryptionUtil.encrypt(note.getHeadline(), (String) session.getAttribute("password")));
        note.setNote(EncryptionUtil.encrypt(note.getNote(), (String) session.getAttribute("password")));
        note.setUser(user);
        noteRepository.save(note);
    }

    public List<Note> searchNotes(User user, String keyword, HttpSession session) throws Exception {
        List<Note> userNotes = this.getAllNotes(user, session);
        return userNotes.stream()
                .filter(note -> note.getNote().toLowerCase().contains(keyword.toLowerCase())
                        || note.getHeadline().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Note getNoteById(Long id, HttpSession session) throws Exception {
        Note note = noteRepository.findById(id).orElse(null);
        assert note != null;
        note.setHeadline(EncryptionUtil.decrypt(note.getHeadline(), (String) session.getAttribute("password")));
        note.setNote(EncryptionUtil.decrypt(note.getNote(), (String) session.getAttribute("password")));
        return note;
    }

    public void updateNote(Note note, HttpSession session) throws Exception {

        note.setHeadline(EncryptionUtil.encrypt(note.getHeadline(), (String) session.getAttribute("password")));
        note.setNote(EncryptionUtil.encrypt(note.getNote(), (String) session.getAttribute("password")));

        noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

}
