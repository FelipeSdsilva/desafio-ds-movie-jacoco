package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieDTO;
import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository movieRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependExistingId;
    private MovieEntity movie;
    private MovieDTO movieDTO;
    private PageImpl<MovieEntity> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        dependExistingId = 2L;
        nonExistingId = 1000L;
        movie = createMovieEntity();
        movieDTO = createMovieDTO();
        page = new PageImpl<>(List.of(movie));

        when(movieRepository.searchByTitle(any(), any())).thenReturn(page);

        when(movieRepository.save(any())).thenReturn(movie);
        when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
        when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(movieRepository.getReferenceById(existingId)).thenReturn(movie);
        when(movieRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(movieRepository.existsById(existingId)).thenReturn(true);
        when(movieRepository.existsById(dependExistingId)).thenReturn(true);
        when(movieRepository.existsById(nonExistingId)).thenReturn(false);

        doNothing().when(movieRepository).deleteById(existingId);
        doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependExistingId);

    }

    @Test
    public void findAllShouldReturnPagedMovieDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieDTO> movieDTOPage = service.findAll("", pageable);

        Assertions.assertNotNull(movieDTOPage);
    }

    @Test
    public void findByIdShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = service.findById(existingId);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            MovieDTO dto = service.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldReturnMovieDTO() {
        MovieDTO result = service.insert(movieDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movieDTO.getId());
    }

    @Test
    public void updateShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = service.update(existingId, movieDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movieDTO.getId());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            MovieDTO result = service.update(nonExistingId, movieDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(()->{
            service.delete(existingId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependExistingId);
        });
    }
}
