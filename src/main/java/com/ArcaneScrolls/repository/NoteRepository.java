package com.ArcaneScrolls.repository;


import com.ArcaneScrolls.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserUsername(String username);

}
