package com.ArcaneScrolls.service;

import com.ArcaneScrolls.config.EncryptionUtil;
import com.ArcaneScrolls.model.Note;
import com.ArcaneScrolls.model.User;
import com.ArcaneScrolls.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes(User user) throws Exception {
        List<Note> notes = noteRepository.findByUserUsername(user.getUsername());
        SecretKey decryptedEncryptionKey = EncryptionUtil.decryptEncryptionKey(user.getEncryptionKey(), user.getPassword());
        for (Note note : notes) {
            String encryptedNoteContent = note.getNote();
            String encryptedNoteHeadline = note.getHeadline();
            String decryptedNoteContent = EncryptionUtil.decrypt(encryptedNoteContent, decryptedEncryptionKey);
            String decryptedNoteHeadline = EncryptionUtil.decrypt(encryptedNoteHeadline, decryptedEncryptionKey);
            note.setNote(decryptedNoteContent);
            note.setHeadline(decryptedNoteHeadline);
        }
        return notes;
    }

    public void saveNoteForUser(User user, Note note) throws Exception {
        SecretKey decryptedEncryptionKey = EncryptionUtil.decryptEncryptionKey(user.getEncryptionKey(), user.getPassword());
        note.setHeadline(EncryptionUtil.encrypt(note.getHeadline(), decryptedEncryptionKey));
        note.setNote(EncryptionUtil.encrypt(note.getNote(), decryptedEncryptionKey));
        note.setUser(user);
        noteRepository.save(note);
    }

    public List<Note> searchNotes(User user, String keyword) throws Exception {
        List<Note> userNotes = this.getAllNotes(user);
        return userNotes.stream()
                .filter(note -> note.getNote().toLowerCase().contains(keyword.toLowerCase())
                        || note.getHeadline().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Note getNoteById(Long id) throws Exception {
        Note note = noteRepository.findById(id).orElse(null);
        assert note != null;
        SecretKey decryptedEncryptionKey = EncryptionUtil.decryptEncryptionKey(note.getUser().getEncryptionKey(), note.getUser().getPassword());
        note.setNote(EncryptionUtil.decrypt(note.getNote(), decryptedEncryptionKey));
        note.setHeadline(EncryptionUtil.decrypt(note.getHeadline(), decryptedEncryptionKey));
        return note;
    }

    public void updateNote(Note note) throws Exception {

        SecretKey decryptedEncryptionKey = EncryptionUtil.decryptEncryptionKey(note.getUser().getEncryptionKey(), note.getUser().getPassword());
        note.setNote(EncryptionUtil.encrypt(note.getNote(), decryptedEncryptionKey));
        note.setHeadline(EncryptionUtil.encrypt(note.getHeadline(), decryptedEncryptionKey));

        noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

}
