package dk.ek.gruppe2.chooseyourfate.service.mysql;

import java.util.List;

import org.springframework.stereotype.Service;

import dk.ek.gruppe2.chooseyourfate.dto.chapter.ChapterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.chapter.CreateChapterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.chapter.UpdateChapterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.ChapterDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Chapter;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ChapterRepository;

//TODO: add handling for moving player to different chapter.
@Service
public class SqlChapterService implements ChapterDataAccess {

    private final ChapterRepository chapterRepository;

    public SqlChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    @Override
    public List<ChapterResponseDTO> getAllChapters() {
        return chapterRepository.findAll()
                .stream()
                .map(ChapterResponseDTO::new)
                .toList();
    }

    @Override
    public ChapterResponseDTO getChapterById(Integer id) {
        return new ChapterResponseDTO(getChapterEntity(id));
    }

    @Override
    public ChapterResponseDTO createChapter(CreateChapterRequestDTO request) {
        Chapter chapter = request.toEntity();
        return new ChapterResponseDTO(chapterRepository.save(chapter));
    }

    @Override
    public ChapterResponseDTO updateChapter(Integer id, UpdateChapterRequestDTO request) {
        Chapter chapter = getChapterEntity(id);
        chapter.setName(request.getName());
        chapter.setScenes(request.getScenes());
        chapter.setCharacters(request.getCharacters());
        return new ChapterResponseDTO(chapterRepository.save(chapter));
    }

    @Override
    public void deleteChapter(Integer id) {
        if (!chapterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Chapter not found with id: " + id);
        }
        chapterRepository.deleteById(id);
    }

    private Chapter getChapterEntity(Integer id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));
    }
}